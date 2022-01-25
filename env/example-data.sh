#!/bin/bash
# Machine 1
curl --location --request POST 'localhost:1026/ngsi-ld/v1/entities' \
--header 'NGSILD-Tenant: impress' \
--header 'Content-Type: application/json' \
--data-raw '{
	"id": "urn:ngsi-ld:crane:test-crane1",
	"type": "Crane",
	"location": {
		"type": "GeoProperty",
		"value": {
			"type": "Point",
			"coordinates": [51.24752, 13.87789]
		}
	},
	"model": {
		"type": "Property",
		"value": "Euro SSG 130"
	},
	"currentCustomer": {
		"type": "Property",
		"value": "Condecta"
	},
	"generalInformation": {
		"type": "Property",
		"value": {
			"maxRadius": {
				"type": "Property",
				"value": 60
			},
			"maxHookHeight": {
				"type": "Property",
				"value": 130
			},
			"maxPayLoad": {
				"type": "Property",
				"value": 8000
			},
			"payloadAtTip": {
				"type": "Property",
				"value": 1650
			}
		}
	},
    "averageUsage": {
        "type": "Property",
        "value": 15
    },
    "averageAvailability": {
        "type": "Property",
        "value": 10
    },
    "lastMaintenance": {
        "type": "Property",
        "value": "2020-12-31"
    },
    "nextMaintenance": {
        "type": "Property",
        "value": "2022-12-15"
    }
} ' || true

# Machine 2
curl --location --request POST 'localhost:1026/ngsi-ld/v1/entities' \
--header 'NGSILD-Tenant: impress' \
--header 'Content-Type: application/json' \
--data-raw '{
	"id": "urn:ngsi-ld:crane:test-crane2",
	"type": "Crane",
	"location": {
		"type": "GeoProperty",
		"value": {
			"type": "Point",
			"coordinates": [51.24752, 13.87789]
		}
	},
	"model": {
		"type": "Property",
		"value": "Euro SSG 90"
	},
	"currentCustomer": {
		"type": "Property",
		"value": "Pader-Bau"
	},
	"generalInformation": {
		"type": "Property",
		"value": {
			"maxRadius": {
				"type": "Property",
				"value": 45
			},
			"maxHookHeight": {
				"type": "Property",
				"value": 90
			},
			"maxPayLoad": {
				"type": "Property",
				"value": 10000
			},
			"payloadAtTip": {
				"type": "Property",
				"value": 2300
			}
		}
	},
    "averageUsage": {
        "type": "Property",
        "value": 12
    },
    "averageAvailability": {
        "type": "Property",
        "value": 9
    },
    "lastMaintenance": {
        "type": "Property",
        "value": "2021-09-04"
    },
    "nextMaintenance": {
        "type": "Property",
        "value": "2022-07-15"
    }
} ' || true

# Machine 3
curl --location --request POST 'localhost:1026/ngsi-ld/v1/entities' \
--header 'NGSILD-Tenant: impress' \
--header 'Content-Type: application/json' \
--data-raw '{
	"id": "urn:ngsi-ld:crane:test-crane3",
	"type": "Crane",
	"location": {
		"type": "GeoProperty",
		"value": {
			"type": "Point",
			"coordinates": [51.24752, 13.87789]
		}
	},
	"model": {
		"type": "Property",
		"value": "Euro SSG 123"
	},
	"currentCustomer": {
		"type": "Property",
		"value": "Fraunhofer Constructions"
	},
	"generalInformation": {
		"type": "Property",
		"value": {
			"maxRadius": {
				"type": "Property",
				"value": 35
			},
			"maxHookHeight": {
				"type": "Property",
				"value": 80
			},
			"maxPayLoad": {
				"type": "Property",
				"value": 4000
			},
			"payloadAtTip": {
				"type": "Property",
				"value": 2500
			}
		}
	},
    "averageUsage": {
        "type": "Property",
        "value": 5
    },
    "averageAvailability": {
        "type": "Property",
        "value": 15
    },
    "lastMaintenance": {
        "type": "Property",
        "value": "2021-09-04"
    },
    "nextMaintenance": {
        "type": "Property",
        "value": "2022-07-15"
    }
} ' || true