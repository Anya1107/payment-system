rootProject.name = "payment-system"

include("individuals-api")
include("user-service")

include("user-service:user-api-client")
include("user-service:user-service")
include("user-service:user-dto")
findProject(":user-service:user-dto")?.name = "user-dto"
include("individuals-api:individuals-dto")
findProject(":individuals-api:individuals-dto")?.name = "individuals-dto"
