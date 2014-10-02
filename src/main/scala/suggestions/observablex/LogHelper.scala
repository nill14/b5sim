package suggestions.observablex

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.slf4j.impl.SimpleLoggerFactory

trait LogHelper {
  
  lazy val logFactory = new SimpleLoggerFactory();
  lazy val log = logFactory.getLogger(this.getClass.getName)
}