Build Management Service 
========================

The purpose is to create a central service layer over our databases. Some of the goals are to :
- Make sure that we don't perform direct call to database from external services but through this service layer.
- Open the project to the open source community by simplifying the installation on various infrastructures and environments.
- Create new APIs in order to offer more features and possibilities.
- Simplify the current implementation mn-build-core so that it can be better maintained, understood, documented and tests.
- Monitor this project using continuous integration systems and quality tools.
- Replace part (if not all) of the mn-build-core module.
- Simplify the mn-build-ant and mn-build-webapp modules
- Many more...

This service will be hosted on a application server such as Glassfish (the recommended server for java ee 7 application).
This project is based on the Java EE 7 full stack platform.