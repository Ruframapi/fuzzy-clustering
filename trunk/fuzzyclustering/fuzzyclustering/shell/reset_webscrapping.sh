#!/bin/bash

stop() {
	echo "Stopping WebScrapping."
   	date=`date +%Y%m%d%H%M%S`
	processId=`ps -fea | grep fuzzyClustering-webscraping-1.0.jar | grep -v " grep " | awk '{ print $2 }'`
	for i in $processId
   	do
      `kill $i`
      	echo "kill $i  [ OK ]" 
   	done
}

start() {
	echo "Starting WebScrapping."
	`echo "java -Xms128m -Xmx640m -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.useLocalHostname=true -jar fuzzyClustering-webscraping-1.0.jar"|at now`
	echo "WebScrapping Started"
}


case "$1" in
   start)
      start
      ;;

   stop)
      stop
      ;;

   restart)
      stop
      start
      ;;
   *)
      echo $"Usage: $0 {start|stop|restart}"
esac
