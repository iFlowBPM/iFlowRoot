As libs devem ser adicionas ao repositorio local do maven de modo a poder compilar o projecto.




mvn install:install-file -Dfile=<path-to-file> -DgroupId=<group-id> \
    -DartifactId=<artifact-id> -Dversion=<version> -Dpackaging=<packaging> -DgeneratePom=true