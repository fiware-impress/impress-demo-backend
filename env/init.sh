#!/bin/bash

# provider company
providerId=$(curl -iL -X 'POST'   'http://localhost:9080/organization'   -H 'accept: */*'   -H 'Content-Type: application/json'   -d '{
               "legalName": "Rent-a-Crane GmbH",
               "email": "crane@rent.org",
               "telephone": "0123/456789",
               "contactType": "Sales",
               "addressCountry": "Germany",
               "addressLocality": "Dresden",
               "addressRegion": "Saxony",
               "postalCode": "01159",
               "streetAddress": "Klingenbergerstraße 3",
               "bankAccount": {
                 "id": "My-Bank-Id",
                 "routingNumber": "123456789",
                 "brand": "Sparkasse"
               }
             }' | awk 'BEGIN {FS=": "}/^location/{print $2}' | sed -e 's/\r//')

echo "Provider: $providerId"
# customer company
customerId=$(curl -iL -X 'POST' \
  'http://localhost:9080/organization' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "legalName": "My Fancy Building AG",
  "email": "fancy@building.org",
  "telephone": "9876/54321",
  "contactType": "Sales",
  "addressCountry": "Germany",
  "addressLocality": "Paderborn",
  "addressRegion": "Nordrhein-Westfalen",
  "postalCode": "33102",
  "streetAddress": "Rolandsweg 123",
  "bankAccount": {
    "id": "My-Other-Bank-Id",
    "routingNumber": "987654321",
    "brand": "Sparkasse"
  }
}' | awk 'BEGIN {FS=": "}/^location/{print $2}' | sed -e 's/\r//')

echo "Customer: $customerId"

# smart service
serviceId=$(curl -iL -X 'POST' \
  'http://localhost:9080/service' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "category": "ConstructionArea",
  "serviceType": "CraneUsage",
  "priceDefinitions": [
    {
      "unitCode": "kg/m",
      "quantity": 1000.0,
      "price": 1.0,
      "priceCurrency": "Euro",
      "measurementPoint": {
        "unitCode": "kg/m",
        "provider": {
          "id": "urn:ngsi-ld:crane:lego-crane",
          "type": "crane"
        },
        "measurementQuery": "select ev.currentWeight? as CurrentValue from pattern [every ev=iotEvent(cast(cast(currentWeight?, int)%10,int)=0 and type=\"crane\")]"
      }
    }
  ]
}' | awk 'BEGIN {FS=": "}/^location/{print $2}' | sed -e 's/\r//')

echo Service: ${serviceId}

# create the offer
offerId=$(curl -iL -X 'POST' \
  'http://localhost:9080/offer' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "areaServed": "Paderborn",
  "itemAvailable": true,
  "category": "ConstructionArea",
  "sellerId": "'${providerId}'",
  "serviceId": "'${serviceId}'"
}' | awk 'BEGIN {FS=": "}/^location/{print $2}' | sed -e 's/\r//')

# create the order - get the id for the offer and both companies first
curl -iL -X 'POST' \
  'http://localhost:9080/order' \
  -H 'accept: */*' \
  -H 'Content-Type: application/json' \
  -d '{
  "acceptedOfferId": "'$offerId'",
  "billingAddress": {
    "addressCountry": "Germany",
    "addressLocality": "Paderborn",
    "addressRegion": "Nordrhein-Westfalen",
    "postalCode": "33102",
    "streetAddress": "Rolandsweg 123"
  },
  "confirmationNumber": "my-confirmation-nr",
  "sellerId": "'$providerId'",
  "customerId": "'$customerId'",
  "discount": 0,
  "discountCurrency": "Euro",
  "paymentMethod": "ByInvoice",
  "orderNumber": "Order-123"
}'
