# Steps followed to generate the client code from the wsdl
- copy the latest soap.wsdl and soap_1.wsdl under \kirona-drs-gateway
- verify the content of build.xml under \kirona-drs-gateway. No changes should be required
- create the directory structure target\classes under \kirona-drs-gateway if it does not exist
- create the directory structure \src\main\java under \kirona-drs-gateway if it does not exist
- open a terminal to \kirona-drs-gateway and: ant gen.kirona_drs
