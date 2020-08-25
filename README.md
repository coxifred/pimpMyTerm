# Welcome to PimpMyTerm repository

![Overview](https://github.com/coxifred/PimpMyTerm/blob/master/doc/pimpMyTerm.png?raw=true)

# What is it ?

This little software allows you to manage your ssh sessions with:
  
  - Resilience (in case of network cutoff between you and pimpMyTerm server)
  - Amiga way of life

*It's look like this:*

![PimpMyTerm](https://github.com/coxifred/PimpMyTerm/blob/master/doc/pimpMyTerm.gif?raw=true)

# What do you need ?

   - Java 10.

# How to install/launch

```bash
git clone https://github.com/coxifred/pimpMyTerm.git
cd pimpMyTerm
./start.sh
```
output:

![PimpMyTerm](https://github.com/coxifred/PimpMyTerm/blob/master/doc/pimpMyTerm2.gif?raw=true)

* Note 1: start.sh or debug.sh launch gradlew in background (nohup).
* Note 2: A log is created under /tmp and named superwatt.log
* Note 3: stop.sh to stop it.

# Configuration

Configuration file is aCore.xml

```xml
<pimpmyterm.core.Core>
  <dataPath>.</dataPath>
  <webServerPort>443</webServerPort>
  <webSocketPort>4430</webSocketPort>
  <webServerIp>192.168.2.186</webServerIp>
  <debug>true</debug>
  <debugJetty>true</debugJetty>
  <adminPassword>admin</adminPassword>
</pimpmyterm.core.Core>
```

# Web Interface

Simply connect with https://<your_ip>

# Docker installation:

  *Create your own configuration:*
  
    - /aCore.xml

  *Create this docker compose file:*
  
  ```bash
  version: "2"
  services:
  "pimpmyterm":
    privileged: true
    image: "coxifred/pimpmyterm"
    container_name: "pimpmyterm"
    restart: always
    ports:
      - "443:443"
      - "4430:4430"
    volumes:
      - /aCore.xml:/aCore.xml
  ```
  
  *Then simply run:* 
  
  ```bash
  docker-compose up -d
  ```
  
  Should be running under https://<your_host>
