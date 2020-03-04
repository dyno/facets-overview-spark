SHELL = /bin/bash

# bin/amm
export PATH := $(shell pwd)/bin:$(PATH)

SPARK_RELEASE := spark-2.4.4-bin-without-hadoop
export SPARK_RELEASE

UNAME := $(shell uname -s)
ifeq ($(UNAME),Linux)
COUSIER_POSTFIX := linux
else ifeq ($(UNAME),Darwin)
COUSIER_POSTFIX := macos
else
$(error $(UNAME) is not supported.)
endif

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
	curl -L -o bin/coursier https://git.io/coursier-cli-$(COUSIER_POSTFIX)
	chmod +x bin/coursier

# https://api.sdkman.io/2/candidates/java/Darwin/versions/list?installed=
JAVA_VERSION := 8.0.232-amzn

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
