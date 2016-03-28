Pre-repqirements: Hadoop 2.6+ and Slider 0.6+

1. Deploy "hadoop-metrics-elasticsearch-sink-1.0.jar" on all compute nodes
"hadoop-metrics-elasticsearch-sink-1.0.jar" is a Hadoop Metrics2 plugin developed by Teraproc. It collects Slider deployed application metrics (i.e., HBase), and stores them in Elasticsearch.
Make sure they are under the same path on all compute nodes. 
Set proper permissions to ensure it can be accessed by Jaguar.
Skip step 2 "Prepare Slider HBase application package" & step 3 " Modify application specification and configuration template" if you use Slider HBased application package that is provided by Teraproc.

2. Prepare Slider HBase application package (Slider 0.80.0 + HBase 0.98.14)
1) Download HBase tar file, and rename the tar ball file to remove the -bin suffix (required by slider)
'''
wget https://archive.apache.org/dist/hbase/0.98.14/hbase-0.98.14-hadoop2-bin.tar.gz
mv hbase-0.98.14-hadoop2-bin.tar.gz hbase-0.98.14-hadoop2.tar.gz
'''

2) Download Slider source code, and build application package for HBase. 
Parameters:
-Dhbase.version=<hbase version>
-Dpkg.name=<file name of HBase tarball>
-Dpkg.src=<folder location where the pkg is available>
-Dpkg.version=<hbase version>

'''
git clone -b releases/slider-0.80.0-incubating https://github.com/apache/incubator-slider.git
cd incubator-slider/app-packages/hbase
mvn clean package -Phbase-app-package -Dhbase.version=0.98.14-hadoop2 -Dpkg.name=hbase-0.98.14-hadoop2.tar.gz -Dpkg.src=/home/ubuntu -Dpkg.version=0.98.14-hadoop2
'''

App package can be found in "incubator-slider/app-packages/hbase/target/slider-hbase-app-package-0.98.14-hadoop2.zip"

3. Modify application specification and configuration template
1) Unzip application package zip files "slider-hbase-app-package-0.98.14-hadoop2.zip". 

2). Go to application package "package\templates" and modify configuration templates "hadoop-metrics2-hbase.properties-GANGLIA-MASTER.j2":
From:
'''
{% if has_metric_collector %}

*.timeline.plugin.urls={{metric_collector_lib}}
hbase.class=org.apache.hadoop.metrics2.sink.timeline.HadoopTimelineMetricsSink
hbase.period=10
hbase.collector={{metric_collector_host}}:{{metric_collector_port}}

jvm.class=org.apache.hadoop.metrics2.sink.timeline.HadoopTimelineMetricsSink
jvm.period=10
jvm.collector={{metric_collector_host}}:{{metric_collector_port}}

rpc.class=org.apache.hadoop.metrics2.sink.timeline.HadoopTimelineMetricsSink
rpc.period=10
rpc.collector={{metric_collector_host}}:{{metric_collector_port}}

hbase.sink.timeline.class=org.apache.hadoop.metrics2.sink.timeline.HadoopTimelineMetricsSink
hbase.sink.timeline.period=10
hbase.sink.timeline.collector={{metric_collector_host}}:{{metric_collector_port}}
hbase.sink.timeline.serviceName-prefix={{app_name}}-master

{% else %}
'''
To:
'''
{% if has_metric_collector %}

*.elasticsearch.plugin.urls={{metric_collector_lib}}
hbase.class=org.apache.hadoop.metrics2.sink.elasticsearch.ElasticsearchMetricsSink
hbase.period=10
hbase.collector={{metric_collector_host}}:{{metric_collector_port}}

jvm.class=org.apache.hadoop.metrics2.sink.elasticsearch.ElasticsearchMetricsSink
jvm.period=10
jvm.collector={{metric_collector_host}}:{{metric_collector_port}}

rpc.class=org.apache.hadoop.metrics2.sink.elasticsearch.ElasticsearchMetricsSink
rpc.period=10
rpc.collector={{metric_collector_host}}:{{metric_collector_port}}

hbase.sink.elasticsearch.class=org.apache.hadoop.metrics2.sink.elasticsearch.ElasticsearchMetricsSink
hbase.sink.elasticsearch.period=10
hbase.sink.elasticsearch.collector={{metric_collector_host}}:{{metric_collector_port}}
hbase.sink.elasticsearch.serviceName-prefix={{app_name}}-master
hbase.sink.elasticsearch.appName-prefix={{app_name}}
hbase.sink.elasticsearch.componentName-prefix={{component_name}}
hbase.sink.elasticsearch.containerId-prefix={{container_id}}

{% else %}
'''

Modify "hadoop-metrics2-hbase.properties-GANGLIA-RS.j2":
From:
'''
{% if has_metric_collector %} 
 
 
*.timeline.plugin.urls={{metric_collector_lib}} 
hbase.class=org.apache.hadoop.metrics2.sink.timeline.HadoopTimelineMetricsSink 
hbase.period=10 
hbase.collector={{metric_collector_host}}:{{metric_collector_port}} 
 
 
jvm.class=org.apache.hadoop.metrics2.sink.timeline.HadoopTimelineMetricsSink 
jvm.period=10 
jvm.collector={{metric_collector_host}}:{{metric_collector_port}} 
 
 
rpc.class=org.apache.hadoop.metrics2.sink.timeline.HadoopTimelineMetricsSink 
rpc.period=10 
rpc.collector={{metric_collector_host}}:{{metric_collector_port}} 

 
hbase.sink.timeline.class=org.apache.hadoop.metrics2.sink.timeline.HadoopTimelineMetricsSink 
hbase.sink.timeline.period=10 
hbase.sink.timeline.collector={{metric_collector_host}}:{{metric_collector_port}} 
hbase.sink.timeline.serviceName={{app_name}}.rs

 
{% else %} 

'''

To:
'''
{% if has_metric_collector %}

*.elasticsearch.plugin.urls={{metric_collector_lib}}
hbase.class=org.apache.hadoop.metrics2.sink.elasticsearch.ElasticsearchMetricsSink
hbase.period=10
hbase.collector={{metric_collector_host}}:{{metric_collector_port}}

jvm.class=org.apache.hadoop.metrics2.sink.elasticsearch.ElasticsearchMetricsSink
jvm.period=10
jvm.collector={{metric_collector_host}}:{{metric_collector_port}}

rpc.class=org.apache.hadoop.metrics2.sink.elasticsearch.ElasticsearchMetricsSink
rpc.period=10
rpc.collector={{metric_collector_host}}:{{metric_collector_port}}

hbase.sink.elasticsearch.class=org.apache.hadoop.metrics2.sink.elasticsearch.ElasticsearchMetricsSink
hbase.sink.elasticsearch.period=10
hbase.sink.elasticsearch.collector={{metric_collector_host}}:{{metric_collector_port}}
hbase.sink.elasticsearch.serviceName-prefix={{app_name}}-rs
hbase.sink.elasticsearch.appName-prefix={{app_name}}
hbase.sink.elasticsearch.componentName-prefix={{component_name}}
hbase.sink.elasticsearch.containerId-prefix={{container_id}}

{% else %}
'''

3)Add following parameters  into "package/scripts/params.py", at the location of behind "#configuration for HBASE_OPTS"
'''
container_id = config['hostLevelParams']['container_id']
component_name = config['componentName']

'''

3) Copy application specification and resource specification files
'''
cp appConfig-default.json /home/ubuntu/appConfig.json
cp resources-default.json /home/ubuntu/resources.json
'''

4) Zip application package
'''
zip -r slider-hbase-app-package-0.98.14-hadoop2.zip ./*
'''

4. Install and start Elasticsearch
'''
curl -o elasticsearch-1.7.1.tar.gz -O -L https://download.elastic.co/elasticsearch/elasticsearch/elasticsearch-1.7.1.tar.gz
tar -xzf elasticsearch-1.7.1.tar.gz -C /usr/local
ln -s /usr/local/elasticsearch-1.7.1  /usr/local/elasticsearch
/usr/local/elasticsearch/bin/elasticsearch
'''

5. Depoly HBase application using Slider
1) Change application specifications file "/home/ubuntu/appConfig.json"
From:
        "site.global.metric_collector_host": "${NN_HOST}",
        "site.global.metric_collector_port": "6188",
        "site.global.metric_collector_lib": "",
to:
        "site.global.metric_collector_host": "${Elasticsearch_HOST}",
        "site.global.metric_collector_port": "9200",
        "site.global.metric_collector_lib": "file://${full_path_of_hadoop-metrics-elasticsearch-sink-1.0.jar}",
'''

2) Deploy and Start the application
slider install-package --name HBASE --package /home/ubuntu/slider-hbase-app-package-0.98.14-hadoop2.zip
slider create hbase1 --template /home/ubuntu/appConfig.json --resources /home/ubuntu/resources.json
'''
Details refer to Slider manual: http://slider.incubator.apache.org/docs/getting_started.html
