# ZAP Project
---

O [ZAP](https://www.owasp.org/index.php/OWASP_Zed_Attack_Proxy_Project) é uma ferramenta capaz de scanear aplicações web em busca de vulnerabilidades
que permitam ataques como XSS e SQL Injection, por exemplo.
Dentre as diversas ferramentas do tipo, o ZAP se destaca devido a sua [maturidade](https://www.owasp.org/index.php/OWASP_Project_Inventory#tab=Flagship_Projects) 
e principalmente por ser o projeto open source [mais ativo](http://owasp.blogspot.com.br/2015/04/owasp-zap-240.html) para segurança em aplicações web.

Outro grande diferencial do ZAP são as facilidades que ele fornece para cenários de auditoria automatizada. O ZAP fornece uma API REST para configuração 
e utilização da ferramenta, possibilitando sua integração no processo de build das aplicações para testes de regressão em segurança.

O **ZAP Project** é uma iniciativa para criar soluções para testes automatizados de segurança utilizando o ZAP.

## Módulos

Os resultados mais visíveis do projeto, do ponto de vista do usuário final, são os plugins para Maven e SonarQube que possibilitam a integração com o ZAP
no processo de build da aplicação. O projeto está dividido em módulos, e mais detalhes sobre cada módulo podem ser encontrados em suas respectivas páginas.

Módulo | Descrição
--- | ---
[ZAP Client API](zap-client-api) | API construída em cima da API do ZAP. É utilizada como base por outros módulos do projeto.
[ZAP CLI](zap-cli) | Programa de linha de comando que pode ser utilizado para configurar e executar testes com o ZAP.
[ZAP Maven Plugin](zap-maven-plugin) | Plugin para o Maven capaz de integrar a análise do ZAP no build da aplicação.
[ZAP SonarQube Plugin](zap-sonar-plugin) | Plugin para o SonarQube que integra os resultados do ZAP como novas métricas.
[ZAP Example](zap-example) | Projeto exemplo que demonstra a utilização dos plugins em uma aplicação cliente.
[ZAP Commons](zap-commons) | Módulo que reúne funcionalidades comuns necessárias para os outros módulos.

---
:zap: ![](https://server119.softplan.com.br:8443/assets/img.jsp?tag=zap&id=zap)