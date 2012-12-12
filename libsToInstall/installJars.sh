mvn install:install-file -Dfile=libGoogleAnalyticsV2.jar -DgroupId=com.google.android.analytics -DartifactId=analytics -Dversion=2.0.3 -Dpackaging=jar
mvn install:install-file -Dfile=robodemo-lib-1.0.1.apklib -DgroupId=com.octo.android.robodemo -DartifactId=robodemo-lib -Dversion=1.0.1 -Dpackaging=apklib
mvn install:install-file -Dfile=robodemo-parent-1.0.1.pom -DgroupId=com.octo.android.robodemo -DartifactId=robodemo-parent -Dversion=1.0.1 -Dpackaging=pom

