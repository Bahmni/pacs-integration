FROM amazoncorretto:8

ENV DCM4CHEE_VERSION=dcm4chee-2.18.1-psql
ENV JBOSS_VERSION=jboss-4.2.3.GA
ENV OVIYAM_VERSION=2.8.1

ENV DCM4CHEE_PATH=/var/lib/bahmni/dcm4chee
ENV JBOSS_PATH=/usr/share/jboss
ENV OVIYAM2_PATH=${DCM4CHEE_PATH}/server/default/deploy/oviyam2.war
ENV DOWNLOAD_PATH=/tmp/artifacts

RUN mkdir -p ${DCM4CHEE_PATH}
RUN mkdir -p ${JBOSS_PATH}
RUN mkdir -p ${DOWNLOAD_PATH}
RUN mkdir -p ${OVIYAM2_PATH}

RUN yum install -y gettext postgresql unzip nc

RUN curl -L -o ${DOWNLOAD_PATH}/dcm4chee.zip "https://repo.mybahmni.org/packages/build/${DCM4CHEE_VERSION}.zip"
RUN unzip -d ${DOWNLOAD_PATH} ${DOWNLOAD_PATH}/dcm4chee.zip
RUN cp -R ${DOWNLOAD_PATH}/${DCM4CHEE_VERSION}/. $DCM4CHEE_PATH

RUN curl -L -o ${DOWNLOAD_PATH}/jboss.zip "https://repo.mybahmni.org/packages/servers/${JBOSS_VERSION}-jdk6.zip"
RUN unzip -d ${DOWNLOAD_PATH} ${DOWNLOAD_PATH}/jboss.zip
RUN cp -R ${DOWNLOAD_PATH}/${JBOSS_VERSION}/. ${JBOSS_PATH}

RUN curl -L -o ${DOWNLOAD_PATH}/Oviyam.zip "https://webwerks.dl.sourceforge.net/project/dcm4che/Oviyam/${OVIYAM_VERSION}/Oviyam-${OVIYAM_VERSION}-bin.zip"
RUN unzip -d ${DOWNLOAD_PATH}/ ${DOWNLOAD_PATH}/Oviyam.zip
RUN cd ${OVIYAM2_PATH} && jar -xvf ${DOWNLOAD_PATH}/Oviyam-${OVIYAM_VERSION}-bin/Oviyam-${OVIYAM_VERSION}-bin/oviyam2.war
COPY package/resources/dcm4chee/oviyam2-web.xml ${OVIYAM2_PATH}/WEB-INF/web.xml

RUN curl -o wait-for.sh 'https://raw.githubusercontent.com/eficode/wait-for/v2.2.3/wait-for'
RUN ${DCM4CHEE_PATH}/bin/install_jboss.sh /usr/share/jboss

COPY package/docker/dcm4chee/templates/pacs-postgres-ds.xml.template /etc/pacs-postgres-ds.xml.template
COPY package/resources/dcm4chee/server.xml ${DCM4CHEE_PATH}/server/default/deploy/jboss-web.deployer/
COPY package/resources/dcm4chee/jboss-service.xml ${DCM4CHEE_PATH}/server/default/conf/
COPY package/resources/dcm4chee/orm2dcm_bahmni.xsl ${DCM4CHEE_PATH}/server/default/conf/dcm4chee-hl7/orm2dcm.xsl
COPY package/resources/dcm4chee/inspectMBean.jsp ${DCM4CHEE_PATH}/server/default/deploy/jmx-console.war/
COPY package/resources/dcm4chee/oviyam2-7-config.xml ${DCM4CHEE_PATH}/server/default/work/jboss.web/localhost/

COPY package/docker/dcm4chee/scripts/startup.sh startup.sh
RUN chmod +x startup.sh
RUN chmod +x wait-for.sh
RUN rm -rf ${DOWNLOAD_PATH}

CMD ["./startup.sh"]
