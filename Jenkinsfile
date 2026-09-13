pipeline {
    agent any

    tools {
        jdk 'Java21'
        maven 'Maven3.9'
    }

    environment {
        CHROME_BIN = '/usr/bin/google-chrome'
    }

    stages {
        stage('Checkout Code') {
            steps {
                checkout scm
            }
        }

        stage('Compile & Static Analysis') {
            steps {
                sh 'mvn clean compile --batch-mode'
            }
        }

        stage('Run Test Suite & JaCoCo Coverage') {
            steps {
                sh 'mvn test verify --batch-mode'
            }
            post {
                always {
                    junit '**/target/surefire-reports/*.xml'
                }
            }
        }

        stage('Archive Artifacts & Coverage') {
            steps {
                archiveArtifacts artifacts: 'target/*.jar, target/site/jacoco/**', allowEmptyArchive: true
            }
        }
    }

    post {
        success {
            echo 'CI Pipeline Build & Test Suite Succeeded!'
        }
        failure {
            echo 'CI Pipeline Build Failed! Check test failure output.'
        }
    }
}
