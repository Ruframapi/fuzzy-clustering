
export PATH=/usr/local/mysql/bin:$PATH

start() {
       `echo "java -Xms128m -Xmx640m -Dcom.sun.management.jmxremote -Dcom.sun.management.jmxremote.ssl=false -Dcom.sun.management.jmxremote.authenticate=false -Djava.rmi.server.useLocalHostname=true -jar $pathProcess $attribute $i > $log 2>&1"|at now`
   done
}
case "$function" in
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
