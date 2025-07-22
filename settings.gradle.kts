rootProject.name = "payment-system"
include("src:main:individuals-api")
findProject(":src:main:individuals-api")?.name = "individuals-api"
include("individuals-api")
include("user-service")
