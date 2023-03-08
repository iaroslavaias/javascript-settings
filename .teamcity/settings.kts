import jetbrains.buildServer.configs.kotlin.*
import jetbrains.buildServer.configs.kotlin.buildSteps.script
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

    vcsRoot(JavaScript_HttpsGithubComIaroslavaiasTeamcityCourseCards)

    buildType(JavaScript_03DeployToStaging)
    buildType(JavaScript_02Chrome)
    buildType(JavaScript_02Firefox)
    buildType(JavaScript_01FastTests)

    template(JavaScript_Template)
}

object JavaScript_01FastTests : BuildType({
    templates(JavaScript_Template)
    id("01FastTests")
    name = "01.Fast Tests"

    params {
        param("Browser", "PhantomJS")
    }
})

object JavaScript_02Chrome : BuildType({
    templates(JavaScript_Template)
    id("02Chrome")
    name = "02.Chrome"

    params {
        param("Browser", "Chrome")
    }

    dependencies {
        snapshot(JavaScript_01FastTests) {
            reuseBuilds = ReuseBuilds.NO
        }
    }
})

object JavaScript_02Firefox : BuildType({
    templates(JavaScript_Template)
    id("02Firefox")
    name = "02.Firefox"

    params {
        param("Browser", "Firefox")
    }

    dependencies {
        snapshot(JavaScript_01FastTests) {
            reuseBuilds = ReuseBuilds.NO
        }
    }
})

object JavaScript_03DeployToStaging : BuildType({
    id("03DeployToStaging")
    name = "03. Deploy To Staging"

    vcs {
        root(JavaScript_HttpsGithubComIaroslavaiasTeamcityCourseCards)
    }

    dependencies {
        snapshot(JavaScript_02Chrome) {
            reuseBuilds = ReuseBuilds.NO
        }
        snapshot(JavaScript_02Firefox) {
            reuseBuilds = ReuseBuilds.NO
        }
    }
})

object JavaScript_Template : Template({
    id("Template")
    name = "Template"

    vcs {
        root(JavaScript_HttpsGithubComIaroslavaiasTeamcityCourseCards)
    }

    steps {
        script {
            name = "Restore NPM Package"
            id = "RUNNER_9"
            scriptContent = "npm install"
        }
        script {
            name = "Browser Tests"
            id = "RUNNER_10"
            scriptContent = "npm test -- --single-run --browsers %Browser% --colors false --reporters teamcity"
        }
    }

    triggers {
        vcs {
            id = "TRIGGER_3"
            branchFilter = ""
        }
    }
})

object JavaScript_HttpsGithubComIaroslavaiasTeamcityCourseCards : GitVcsRoot({
    id("HttpsGithubComIaroslavaiasTeamcityCourseCards")
    name = "https://github.com/iaroslavaias/teamcity-course-cards"
    url = "https://github.com/iaroslavaias/teamcity-course-cards"
    branch = "refs/heads/master"
})
