package tests

import com.amazonaws.regions.Regions
import com.amazonaws.services.elasticbeanstalk.{AWSElasticBeanstalk, AWSElasticBeanstalkClientBuilder}
import org.scalatest.FunSuite

import scala.collection.JavaConverters.asScalaBufferConverter

class BeanstalkSDK extends FunSuite {
  ignore("run sdk") {
    val client: AWSElasticBeanstalk = AWSElasticBeanstalkClientBuilder.standard()
      .withRegion(Regions.EU_WEST_1)
      .build()
    val envs = client.describeEnvironments().getEnvironments.asScala.toList
    assert(envs.nonEmpty)
  }
}
