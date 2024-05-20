import net.kyori.indra.IndraExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.credentials.PasswordCredentials
import org.gradle.api.publish.PublishingExtension
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.credentials
import org.incendo.cloudbuildlogic.city
import org.incendo.cloudbuildlogic.jmp
import java.net.URI

class CloudPublishingConventions : Plugin<Project> {
    override fun apply(target: Project) {
        target.plugins.apply("org.incendo.cloud-build-logic.publishing")

        target.extensions.configure<PublishingExtension> {
            repositories {
                maven {
                    name = "pgmRepo"

                    val releasesRepoUrl = URI("https://repo.pgm.fyi/releases")
                    val snapshotsRepoUrl = URI("https://repo.pgm.fyi/snapshots")
                    url = if (target.version.toString().endsWith("SNAPSHOT")) snapshotsRepoUrl else releasesRepoUrl

                    credentials(PasswordCredentials::class)
                }
            }
        }

        target.extensions.configure(IndraExtension::class) {
            github("Incendo", "cloud") {
                ci(true)
            }
            mitLicense()

            configurePublications {
                pom {
                    developers {
                        city()
                        jmp()
                    }
                }
            }
        }
    }
}
