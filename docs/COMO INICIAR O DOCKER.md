# Para subir esta aplicação no Docker, siga os seguintes passos.

## Passo 1: Compilar o projeto

  Em um terminal na pasta raiz do projeto e utilize o comando:
  ```bash
  mvn clean install -Dskiptests
  ```
  Execute e aguarde o "BUILD SUCESS"

## Passo 2: Criar as imagens no docker

  Em um terminal na pasta raiz do projeto e utilize o comando:
  ```bash
  docker compose build
  ```
  O docker criara a imagem de cada microsserviço no docker, estas imagens serão utilizadas para subir os containeres de cada microsserviço.

## Passo 3: Subir os containeres

  Em um terminal na pasta raiz do projeto e utilize o comando:
  ```bash
  docker compose up -d
  ```
  O Docker mostrara o resultado da tentativa de subir os containeres na tela, quando todos estiverem com "Running" ou "Starting", o projeto está pronto para ser usado.

## Caso precise desligar os containeres utilize 
  ```bash
  docker compose down
  ```

## Passo 4: Subir o dump do banco
 Em um terminal na pasta raiz do projeto e utilize o comando:
  ```bash
  docker exec -i nome-do-container mysql -uroot -proot theater_db < sql/dump.sql
  ```
+Provavelmente o nome do container é teatro-hexagonal-mysql-1, mas confira utilizando:
  ```bash
  docker ps
  ```
### Consulte o API_ROUTES.md para mais informações das rotas.
