#!/bin/bash

TEMP=`getopt -o V --long version -n 'SIP Communicator' -- "$@"`

if [ $? != 0 ] ; then echo "Terminating..." >&2 ; exit 1 ; fi

eval set -- "$TEMP"

while true ; do
        case "$1" in
                -V|--version) echo "SIP Communicator version 1.0-alpha2.nightly.build"; exit 0;;
                --) shift ; break ;;
                *) echo "Internal error!" ; exit 1 ;;
        esac
done

javabin=`which java`

SCDIR=/usr/share/sip-communicator
LIBPATH=$SCDIR/lib

#set add LIBPATH to LD_LIBRARY_PATH for any sc natives (e.g. jmf .so's)
export LD_LIBRARY_PATH=$LD_LIBRARY_PATH:$LIBPATH/native

#make LD_PRELOAD use libaoss so that java doesn't hog on the audio device.
export LD_PRELOAD=/usr/lib/libaoss.so

#create .sip-commnicator/log in home or otherwise java.util.logging will freak
mkdir -p $HOME/.sip-communicator/log

cd $SCDIR

if [ -f $javabin ]
then
        $javabin -classpath	"$LIBPATH/felix.jar:$LIBPATH/kxml-min.jar:$LIBPATH/servicebinder.jar:$LIBPATH/bundle/org.apache.felix.servicebinder-0.8.0-SNAPSHOT.jar:$LIBPATH/BrowserLauncher2.jar:$SCDIR/sc-bundles/util.jar" -Dicq.custom.message.charset=windows-1251 -Dfelix.config.properties=file:$LIBPATH/felix.client.run.properties -Djava.util.logging.config.file=$LIBPATH/logging.properties org.apache.felix.main.Main
        exit $?
fi
