SHELL = /bin/bash


ifndef VIRTUAL_ENV
WITH_VENV := pipenv run
else
WITH_VENV :=
endif


init: pipenv-install
	git submodule update

pipenv-install:
	pipenv install --dev

install-ipykernel:
	$(WITH_VENV) python -m ipykernel install --user --name python-facets --display-name "python(facets)"

install-nbextension:
	@ # https://github.com/PAIR-code/facets#usage-in-google-colabratoryjupyter-notebooks
	$(WITH_VENV) jupyter nbextension install --user facets/facets-dist/
	@ echo IGNORE the above "jupyter nbextension enable ..."

jupyter: install-ipykernel install-nbextension
	$(WITH_VENV) jupyter notebook                  \
	  --NotebookApp.open_browser=False             \
	  --NotebookApp.port=8036                      \
	  --NotebookApp.allow_origin="*"               \
	  --NotebookApp.iopub_data_rate_limit=10000000 \
	# END
