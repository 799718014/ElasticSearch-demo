# nacos-sentinel-seata-springcloudAlibab
springboot集成ElasticSearch

#### 介绍
使用ElasticSearch实现商品搜索高亮显示功能，通过springboot集成ElasticSearch组件：

####   ElasticSearch三件套
ElasticSearch官网：https://www.elastic.co/cn

ElasticSearch可视化界面：https://github.com/mobz/elasticsearch-head

kibana：https://www.elastic.co/cn/downloads/past-releases/kibana-7-6-1

注意：ES版本和kibana版本要一致

####   大致业务流程梳理：
1、通过kibana建ES索引

2、程序把商品信息存到到ES的索引中

3、获取商品信息接口，通过es高亮类设置高亮标签

4、es搜索条件执行返回结果list

5、循环List，解析高亮字段，高亮字段替换结果list中对应的值，例如title属性

6、返回list，前端渲染

}
