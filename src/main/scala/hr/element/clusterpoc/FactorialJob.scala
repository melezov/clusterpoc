package hr.element.clusterpoc

case class FactorialJob(ordinal: Int) // 20
case class FactorialResult(result: BigInt, tookMs: Long) // 2432902008176640000
case class FactorialFailed(job: FactorialJob, reason: String)

case object BackendRegistration
