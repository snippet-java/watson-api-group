cd ..
path=$(pwd)

export CLASSPATH=$path/target/nlc-201-news-java-student.jar:$path/lib/opencsv-3.3.jar:$path/lib/java-sdk-3.5.3-jar-with-dependencies.jar:$path/lib/json-20160212.jar

cd resources

java -cp $CLASSPATH com.ibm.itso.ed600r01.nlc.news.Evaluate $1 $2 $3 "$4"

