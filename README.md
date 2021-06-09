# CryptoCurrency Wallet Simulator API

API that allows to simulate currency wallets allowing the operations over it. The simulator design is a simple POC, it allows CRUD operations of the Wallets, and
allows for the transfer operations in, and between wallets.

## Steps to setup
Make sure you are using JDK 1.8 and Maven 3.x, and have Internet connection.

**1. Clone the application**
```sh
$ git clone https://github.com/jolijavetzky/currency-wallet-api.git
```
**2. Build the project and run the tests by running**
```sh
$ cd currency-wallet-api/
$ mvn clean package
```
**3. Run the application by one of these two methods**
- Execute as Java JAR application
```sh
$ java -jar target/currencywalletapi-0.0.1-SNAPSHOT.jar
```  
- Execute as Spring Boot application
```sh
$ mvn spring-boot:run
```  
**4.- Explore the rest api by browsing**
```sh
http://localhost:8080/
```  
