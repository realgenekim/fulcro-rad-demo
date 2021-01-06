cljs:
	shadow-cljs -A:f3-dev:rad-dev:i18n-dev server

report:
	npx shadow-cljs run shadow.cljs.build-report main report.html

release:
	TIMBRE_LEVEL=:warn npx shadow-cljs release main

run-tests:
	bin/kaocha --plugin notifier --watch

cljs-compile:
	npx shadow-cljs watch main
