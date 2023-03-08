import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildFeatures.perfmon
import jetbrains.buildServer.configs.kotlin.buildSteps.DotnetMsBuildStep
import jetbrains.buildServer.configs.kotlin.buildSteps.NUnitStep
import jetbrains.buildServer.configs.kotlin.buildSteps.dotnetMsBuild
import jetbrains.buildServer.configs.kotlin.buildSteps.maven
import jetbrains.buildServer.configs.kotlin.buildSteps.nuGetInstaller
import jetbrains.buildServer.configs.kotlin.buildSteps.nuGetPack
import jetbrains.buildServer.configs.kotlin.buildSteps.nunit
import jetbrains.buildServer.configs.kotlin.projectFeatures.buildReportTab
import jetbrains.buildServer.configs.kotlin.triggers.vcs
import jetbrains.buildServer.configs.kotlin.vcs.GitVcsRoot

/*
The settings script is an entry point for defining a TeamCity
project hierarchy. The script should contain a single call to the
project() function with a Project instance or an init function as
an argument.

VcsRoots, BuildTypes, Templates, and subprojects can be
registered inside the project using the vcsRoot(), buildType(),
template(), and subProject() methods respectively.

To debug settings scripts in command-line, run the

    mvnDebug org.jetbrains.teamcity:teamcity-configs-maven-plugin:generate

command and attach your debugger to the port 8000.

To debug in IntelliJ Idea, open the 'Maven Projects' tool window (View
-> Tool Windows -> Maven Projects), find the generate task node
(Plugins -> teamcity-configs -> teamcity-configs:generate), the
'Debug' option is available in the context menu for the task.
*/

version = "2022.10"

project {
    description = "Contains all other projects"

    features {
        buildReportTab {
            id = "PROJECT_EXT_1"
            title = "Code Coverage"
            startPage = "coverage.zip!index.html"
        }
    }

    cleanup {
        baseRule {
            preventDependencyCleanup = false
        }
    }

    subProject(TeamcityCourseSpringPetclinic)
    subProject(TeamcityCourseAspnetIdentityMongo)
}


object TeamcityCourseAspnetIdentityMongo : Project({
    name = "Teamcity Course Aspnet Identity Mongo"

    vcsRoot(TeamcityCourseAspnetIdentityMongo_HttpsGithubComG0t4teamcityCourseAspnetIdentityMongoRefsHeadsMaster)

    buildType(TeamcityCourseAspnetIdentityMongo_Build)
})

object TeamcityCourseAspnetIdentityMongo_Build : BuildType({
    name = "Build"

    artifactRules = """build\AspNet.Identity.MongoDB.2.1.0.nupkg"""

    vcs {
        root(TeamcityCourseAspnetIdentityMongo_HttpsGithubComG0t4teamcityCourseAspnetIdentityMongoRefsHeadsMaster)
    }

    steps {
        nuGetInstaller {
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            projects = "src/AspNet.Identity.MongoDB.sln"
            updatePackages = updateParams {
            }
        }
        dotnetMsBuild {
            projects = "src/AspNet.Identity.MongoDB.sln"
            version = DotnetMsBuildStep.MSBuildVersion.V16
            configuration = "Release"
            args = "-restore -noLogo"
            sdk = "4.5 4.5.1"
            param("dotNetCoverage.dotCover.home.path", "%teamcity.tool.JetBrains.dotCover.CommandLineTools.DEFAULT%")
        }
        nunit {
            nunitVersion = NUnitStep.NUnitVersion.NUnit_2_6_4
            runtimeVersion = NUnitStep.RuntimeVersion.v4_0
            includeTests = """build\tests\Tests.dll"""
            reduceTestFeedback = true
            coverage = dotcover {
            }
        }
        nuGetPack {
            enabled = false
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "src/AspNet.Identity.MongoDB/AspNet.Identity.MongoDB.csproj"
            outputDir = "build"
            cleanOutputDir = false
        }
        nuGetPack {
            toolPath = "%teamcity.tool.NuGet.CommandLine.DEFAULT%"
            paths = "src/AspNet.Identity.MongoDB/AspNet.Identity.MongoDB.csproj"
            outputDir = "build"
            cleanOutputDir = false
            publishPackages = true
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object TeamcityCourseAspnetIdentityMongo_HttpsGithubComG0t4teamcityCourseAspnetIdentityMongoRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/iaroslavaias/teamcity-course-aspnet-identity-mongo"
    url = "https://github.com/iaroslavaias/teamcity-course-aspnet-identity-mongo"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
})


object TeamcityCourseSpringPetclinic : Project({
    name = "Teamcity Course Spring Petclinic"

    vcsRoot(TeamcityCourseSpringPetclinic_HttpsGithubComG0t4teamcityCourseSpringPetclinicRefsHeadsMaster)

    buildType(TeamcityCourseSpringPetclinic_Build)
})

object TeamcityCourseSpringPetclinic_Build : BuildType({
    name = "Build"

    artifactRules = """target\petclinic.war"""

    vcs {
        root(TeamcityCourseSpringPetclinic_HttpsGithubComG0t4teamcityCourseSpringPetclinicRefsHeadsMaster)
    }

    steps {
        maven {
            goals = "clean package"
            runnerArgs = "-Dmaven.test.failure.ignore=true"
        }
    }

    triggers {
        vcs {
        }
    }

    features {
        perfmon {
        }
    }
})

object TeamcityCourseSpringPetclinic_HttpsGithubComG0t4teamcityCourseSpringPetclinicRefsHeadsMaster : GitVcsRoot({
    name = "https://github.com/g0t4/teamcity-course-spring-petclinic#refs/heads/master"
    url = "https://github.com/g0t4/teamcity-course-spring-petclinic"
    branch = "refs/heads/master"
    branchSpec = "refs/heads/*"
})
