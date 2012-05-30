ANT = ant


all: backend webservice

clean: backend_clean webservice_clean


backend : backend_clean
	@echo "-->SeqWare-QueryEngine: Building Backend<--"
	@cd backend && $(ANT) all

backend_clean :
	@echo "-->SeqWare-QueryEngine: Cleaning Backend<--"
	@cd backend && $(ANT) clean


webservice : webservice_clean
	@echo "-->SeqWare-QueryEngine: Building Webservice<--"
	@cd webservice && $(ANT) all

webservice_clean :
	@echo "-->SeqWare-QueryEngine: Cleaning Webservice<--"
	@cd webservice && $(ANT) clean
