package suggestions.observablex

import org.slf4j.Logger
import org.slf4j.LoggerFactory

trait LogHelper {
    lazy val log = LoggerFactory.getLogger(this.getClass.getName)
}