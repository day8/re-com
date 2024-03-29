# [Choice] Java version (use -bullseye variants on local arm64/Apple Silicon): 11, 17, 11-bullseye, 17-bullseye, 11-buster, 17-buster

# MT: removed this ARG stuff and went with hardcoded version instead  because it was giving errors. 
# ARG VARIANT=${templateOption:imageVariant}
# FROM mcr.microsoft.com/devcontainers/java:${VARIANT}    

FROM mcr.microsoft.com/devcontainers/java:21

# [Optional] Clojure version
ARG CLOJURE_VERSION=1.10.3

# [Optional] Clojure tools version
ARG CLOJURE_CLI_VERSION=1.10.3.1075

# [Optional] Leiningen version
ARG LEININGEN_VERSION="stable"

# [Optional] POLYLITH version
ARG POLYLITH_VERSION="0.2.13-alpha"

ARG INSTALL_CLOJURE_CLI="true"
# # [Option] Install Clojure CLI tool
# ARG INSTALL_CLOJURE_CLI="${templateOption:installClojureCli}"

ARG INSTALL_LEININGEN="true"
# # [Option] Install Leiningen
# ARG INSTALL_LEININGEN="${templateOption:installLeiningen}"

ARG INSTALL_POLYLITH="true"
# # [Option] Install Polylith
# ARG INSTALL_POLYLITH="${templateOption:installPolylith}"

RUN if [ "${INSTALL_CLOJURE_CLI}" = "true" ]; then \
    apt-get update \
    && apt-get -y install rlwrap \
    && curl -OL "https://download.clojure.org/install/linux-install-${CLOJURE_CLI_VERSION}.sh" \
    && chmod +x linux-install-${CLOJURE_CLI_VERSION}.sh \
    && /linux-install-${CLOJURE_CLI_VERSION}.sh \
    && rm /linux-install-${CLOJURE_CLI_VERSION}.sh \
    && su vscode -c "clj --version"; fi


RUN if [ "${INSTALL_LEININGEN}" = "true" ]; then \
    curl -OL "https://raw.githubusercontent.com/technomancy/leiningen/${LEININGEN_VERSION}/bin/lein" \
    && chmod +x lein \
    && mv lein /usr/local/sbin; fi

# Cache Clojure and dependencies
RUN if [ "${INSTALL_LEININGEN}" = "true" ]; then \
    su vscode -c " cd ~ \
    && echo '(defproject dummy \"\" :dependencies [[org.clojure/clojure \"'${CLOJURE_VERSION}'\"]])' > project.clj \
    && lein deps \
    && rm project.clj"; fi

RUN if [ "${INSTALL_POLYLITH}" = "true" ]; then \
    curl -OL "https://github.com/polyfy/polylith/releases/download/v${POLYLITH_VERSION}/poly-${POLYLITH_VERSION}.jar" \
    && mkdir -p /usr/local/polylith \
    && mv poly-$POLYLITH_VERSION.jar /usr/local/polylith \
    && echo '#!/bin/sh\nARGS=""\nwhile [ "$1" != "" ] ; do\n  ARGS="$ARGS $1"\n  shift\ndone\nexec "java" $JVM_OPTS "-jar" "/usr/local/polylith/poly-'$POLYLITH_VERSION'.jar" $ARGS\n' > /usr/local/sbin/poly \
    && chmod +x /usr/local/sbin/poly \
    && /usr/local/sbin/poly version; fi

# [Choice] Node.js version: none, lts/*, 16, 14, 12, 10
#ARG NODE_VERSION="${templateOption:nodeVersion}"
ARG NODE_VERSION="16"
RUN if [ "${NODE_VERSION}" != "none" ]; then su vscode -c "umask 0002 && . /usr/local/share/nvm/nvm.sh && nvm install ${NODE_VERSION} 2>&1"; fi

# [Optional] Uncomment this section to install additional OS packages.
# RUN apt-get update && export DEBIAN_FRONTEND=noninteractive \
#     && apt-get -y install --no-install-recommends <your-package-list-here>

# [Optional] Uncomment this line to install global node packages.
# RUN su vscode -c "source /usr/local/share/nvm/nvm.sh && npm install -g <your-package-here>" 2>&1

# Clean up package lists
RUN apt-get autoremove -y && apt-get clean -y && rm -rf /var/lib/apt/lists/*
