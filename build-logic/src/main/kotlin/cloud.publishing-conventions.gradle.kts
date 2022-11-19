plugins {
    id("net.kyori.indra.publishing")
}

signing {
    val signingKey: String? by project
    val signingPassword: String? by project
    useInMemoryPgpKeys(signingKey, signingPassword)
}

publishing {
    repositories {
        maven {
            name = "pgmRepo"

            val releasesRepoUrl = uri("https://repo.pgm.fyi/releases")
            val snapshotsRepoUrl = uri("https://repo.pgm.fyi/snapshots")
            url = if (version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

            credentials(PasswordCredentials::class)
        }
    }
}

indra {
    github("Incendo", "cloud") {
        ci(true)
    }
    mitLicense()

    configurePublications {
        pom {
            developers {
                developer {
                    id.set("Sauilitired")
                    name.set("Alexander Söderberg")
                    url.set("https://alexander-soderberg.com")
                    email.set("contact@alexander-soderberg.com")
                }
            }
        }
    }
}
