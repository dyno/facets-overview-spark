SHELL = /bin/bash

UNAME := $(shell uname -s)
ifeq ($(UNAME),Linux)
COURSIER_POSTFIX := linux
else ifeq ($(UNAME),Darwin)
COURSIER_POSTFIX := macos
else
$(error $(UNAME) is not supported.)
endif


SPARK_RELEASE := spark-2.4.5-bin-without-hadoop
SPARK_VERSION := $(shell awk -v FS=- '{print $$2;}' <<<$(SPARK_RELEASE))
$(info SPARK_RELEASE=$(SPARK_RELEASE), SPARK_VERSION=$(SPARK_VERSION))

# https://api.sdkman.io/2/candidates/java/Darwin/versions/list?installed=
JAVA_VERSION := 8.0.232-amzn

export PATH := $(shell pwd)/bin:$(PATH)
# bin/amm use it to tell whether to start spark with scala 2.11 or 2.12
export SPARK_RELEASE


.DEFAULT_GOAL: amm
.PHONY: amm
amm: bin/amm bin/coursier
	source ~/.sdkman/bin/sdkman-init.sh         \
	&& amm  --no-home-predef --predef predef.sc \
	# END

.PHONY: install-ammonite
install-ammonite: install-coursier install-java

.PHONY: install-coursier
install-coursier: bin/coursier
bin/coursier:
	@echo "-- install [coursier](https://get-coursier.io/docs/cli-overview.html#installation)"
	mkdir -p bin
	curl -L -o bin/coursier https://git.io/coursier-cli-$(COURSIER_POSTFIX)
	chmod +x bin/coursier

.PHONY: install-java
install-java: install-sdkman
	@echo "-- install java/scala with sdkman"
	@# https://sdkman.io/usage#config
	sed -i .bak-$$(date +'%Y%m%d-%H%M%S') 's/sdkman_auto_answer=false/sdkman_auto_answer=true/' ~/.sdkman/etc/config
	source ~/.sdkman/bin/sdkman-init.sh               \
	  && sdk selfupdate force                         \
	  && (sdk install java $(JAVA_VERSION) || true)   \
	# END

.PHONY: install-sdkman
install-sdkman:
	@echo "-- install [sdkman](https://sdkman.io/install)"
	@# XXX: sdkman is a shell function, and can not be initialized in make env.
	@if [[ ! -d ~/.sdkman ]]; then            \
	  curl -s "https://get.sdkman.io" | bash; \
	fi                                        \
	# END

$(SPARK_RELEASE):
	curl -LO http://mirrors.ibiblio.org/apache/spark/spark-$(SPARK_VERSION)/$(SPARK_RELEASE).tgz \
	  && tar xvf $(SPARK_RELEASE).tgz                                                                  \
	# END
