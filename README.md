# trino-mode

Example of Trino UDFs Plugin to encrypt and decrypt values with a password.

## Introduction

In [Trino](https://trino.io) you can create new Plugins by implementing interfaces and override methods defined by the [SPI](https://trino.io/docs/current/develop/spi-overview.html).

Plugins can provide additional Connectors, Types, Functions and with this project we implement 2 new [SQL Functions](https://trino.io/docs/current/develop/functions.html) (or UDFs / User-Defined Functions) to encrypt or decrypt a value (from a column or not) with a password.

The method used to encrypt a value is [PBE](http://www.crypto-it.net/eng/theory/pbe.html) (Password Based Encryption), a method where the encryption key (which is binary data) is derived from a password (string). PBE is using an encryption key generated from a password, random salt and number of iterations.
Details on [Java implementation](https://www.javamex.com/tutorials/cryptography/password_based_encryption.shtml), we use the [PBEWithMD5AndDES](https://www.javamex.com/tutorials/cryptography/pbe_key_derivation.shtml) mode.



## Build

```
mvn clean package
```

If you want skip unit tests, please run:
```
mvn clean package -DskipTests
```

It will generate a **trino-mode-{version}.jar** and **trino-mode-{version}** folder in target directory.
   
## Deploy

Copy the **trino-mode-{version}** folder from **target** directory in your Trino **plugin** directory and restart Trino server.
   
```bash
% cp -R ./target/trino-mode-{version} <trino-server-folder>/plugin/trino-mode

% <trino-server-folder>/bin/launcher restart
```

Then you should find 1 new function **mode0** if you list all available functions of your Trino server with **SHOW FUNCTIONS** SQL command:
``` 
"mode","varchar","varchar, varchar","scalar","true","UDF to find mode given a set of values"


``` 
## Usage

With a local trino server and trino CLI you can test the UDFs with:
``` 
%<trino-cli-folder>/trino --execute "SELECT mode([1,2,3,4])"
```
