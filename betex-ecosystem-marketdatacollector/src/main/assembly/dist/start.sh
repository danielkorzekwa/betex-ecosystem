export JAVA_HOME=/usr/java/jdk1.6.0_17
#export DEBUG="-Xdebug -Xrunjdwp:transport=dt_socket,address=8000,server=y,suspend=n"
export JMX="-Dcom.sun.management.jmxremote.port=20000 -Dcom.sun.management.jmxremote.authenticate=false -Dcom.sun.management.jmxremote.ssl=false"
export MARKETDATACOLLECTOR_OPTIONS="-DbfUser=xxx -DbfPassword=xxx -DbfProductId=xxx -Dcouchdb.url=xxx -DmarketTradedVolumeDb.name=xxx -DmarketDetailsDb.name=xxx"
$JAVA_HOME/bin/java $JMX $DEBUG -Xmx512m $MARKETDATACOLLECTOR_OPTIONS -cp ./lib/betex-ecosystem-marketdatacollector-${project.version}.jar dk.betex.ecosystem.marketdatacollector.MarketDataCollectorApp