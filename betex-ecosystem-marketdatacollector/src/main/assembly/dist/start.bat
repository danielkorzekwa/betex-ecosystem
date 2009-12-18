rem set DEBUG=-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n
set JMX=-Dcom.sun.management.jmxremote.port=20000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false
set MARKETDATACOLLECTOR_OPTIONS=-DbfUser=xxx -DbfPassword=xxx -DbfProductId=xxx -DmarketTradedVolumeDb.url=xxx -DmarketTradedVolumeDb.name=xxx
java %JMX% %DEBUG% -Xmx512m %MARKETDATACOLLECTOR_OPTIONS% -cp ./lib/betex-ecosystem-marketdatacollector-${project.version}.jar dk.betex.ecosystem.marketdatacollector.MarketDataCollectorApp