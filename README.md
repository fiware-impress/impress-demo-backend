# BaaS - Backend implementation of the impress demo frontend 

Backend for the Impress-Demonstrator application. For running a full setup, go to the [env-directory](./env) :

1. Run 
```shell
    docker-compose -f docker-compose-with-service.yaml up
```
Wait until all components are running: 
```shell
CONTAINER ID   IMAGE                                         COMMAND                  CREATED          STATUS                    PORTS                                                      NAMES
8ca39d75a735   quay.io/wi_stefan/demo-backend:latest         "java -cp /app/resou…"   35 seconds ago   Up 33 seconds             0.0.0.0:4010->8080/tcp, :::4010->8080/tcp                  env_baas-backend_1
cbb52ca1c86d   fiware/perseo:1.17.0                          "docker-entrypoint.s…"   37 seconds ago   Up 34 seconds (healthy)   0.0.0.0:9090->9090/tcp, :::9090->9090/tcp                  env_perseo-fe_1
1ed1b075a2a9   quay.io/wi_stefan/contract-service:0.1.5   "java -cp /app/resou…"   37 seconds ago   Up 35 seconds             0.0.0.0:9080->8080/tcp, :::9080->8080/tcp                  env_contract-service_1
2b31280bd1ae   wistefan/machine-simulator                    "java -cp /app/resou…"   37 seconds ago   Up 35 seconds             8080/tcp, 8443/tcp                                         env_machine-simulator_1
97b06e76b8b4   quay.io/wi_stefan/lego-subscriber             "lego-subscriber"        37 seconds ago   Up 35 seconds                                                                        env_lego-subsciber_1
fa4e3ca0a6b2   wistefan/wm-ngsi-connector                    "/bin/sh /app/entryp…"   37 seconds ago   Up 34 seconds                                                                        env_mongodb-to-ngsibroker_1
09cd4c5b3dad   fiware/orion-ld:1.0.1                         "orionld -fg -multis…"   38 seconds ago   Up 30 seconds (healthy)   0.0.0.0:1026->1026/tcp, :::1026->1026/tcp                  env_orion-ld_1
7f65b316e88e   wistefan/wm-mintaka-connector                 "/bin/sh /app/entryp…"   38 seconds ago   Up 37 seconds                                                                        env_mintaka-to-mongodb_1
adf231bd299b   fiware/perseo-core                            "/code/perseo_core-e…"   38 seconds ago   Up 36 seconds (healthy)   8080/tcp                                                   env_perseo-core_1
6226f945b4cd   wistefan/wm-runtime                           "/bin/sh /app/entryp…"   38 seconds ago   Up 37 seconds                                                                        env_model-runtime_1
5d93ba048103   mongo:4.0                                     "docker-entrypoint.s…"   40 seconds ago   Up 38 seconds (healthy)   27018/tcp, 0.0.0.0:27018->27017/tcp, :::27018->27017/tcp   env_mongo-db-wm_1
665e822bcb2b   mongo:4.0                                     "docker-entrypoint.s…"   40 seconds ago   Up 38 seconds (healthy)   0.0.0.0:27017->27017/tcp, :::27017->27017/tcp              env_mongo-db_1
31ef5f29d655   fiware/mintaka:0.3.42                         "java -cp /app/resou…"   40 seconds ago   Up 39 seconds             0.0.0.0:7080->8080/tcp, :::7080->8080/tcp                  env_mintaka_1
9eb5be01097a   wistefan/itd                                  "dotnet run"             40 seconds ago   Up 38 seconds             80/tcp, 0.0.0.0:5000->5000/tcp, :::5000->5000/tcp          env_frontend_1
c9b56a268dd0   eclipse-mosquitto                             "/docker-entrypoint.…"   40 seconds ago   Up 39 seconds             0.0.0.0:1883->1883/tcp, :::1883->1883/tcp                  env_mosquitto_1
8b74f54738d0   timescale/timescaledb-postgis:latest-pg12     "docker-entrypoint.s…"   40 seconds ago   Up 38 seconds (healthy)   0.0.0.0:5432->5432/tcp, :::5432->5432/tcp                  env_timescale_1
3977537f32ff   wistefan/dn                                   "python3 /usr/src/ap…"   40 seconds ago   Up 39 seconds             0.0.0.0:5001->5001/tcp, :::5001->5001/tcp                  env_dn_1
```
2. Run:
```shell
 ./init.sh
```
to setup two organizations(provider and customer), one smart-service(crane per use), one offer and the order to actually setup the service execution. See the file for details.
