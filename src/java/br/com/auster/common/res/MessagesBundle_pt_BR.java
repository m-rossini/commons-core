/*
 * Copyright (c) 2004 Auster Solutions do Brasil. All Rights Reserved.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" 
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, 
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR 
 * PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR 
 * CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, 
 * EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, 
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; 
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, 
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE 
 * OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, 
 * EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE. 
 * 
 * Created on 26/10/2004
 */
package br.com.auster.common.res;

import java.util.ListResourceBundle;

/**
 * @author Ricardo Barone
 * @version $Id: MessagesBundle_pt_BR.java 251 2006-06-10 19:08:34Z rbarone $
 */
public class MessagesBundle_pt_BR extends ListResourceBundle {

   public final Object[][] getContents()
   {
      return contents;
   }

   static final Object[][] contents = {
  	 
  	 // ConnectionManager.java
  	 {"reloadConnPoolConf", "Recarregando configuração do Connection Pool Manager..."},
  	 {"done", "Pronto."},
  	 {"addingServer", "Adicionando o servidor à lista de servidores: {0}"},
  	 {"couldntAddServer", "Não foi possível adicionar o servidor {0} à lista de servidores."},
  	 {"errorConnecting", "Erro ao abrir conexão com o servidor {0}."},
  	 {"serversDown", "Servidores caídos ou inalcançáveis."},
  	 {"problemDisconnectingServer", "Problemas ao desconectar do servidor {0}."},

  	 // ConnectionPoolManager.java
  	 {"closingOpenedServerConns", "Fechando todas as conexões abertas do servidor..."},
  	 {"problemClosingConnection", "Problemas ao fechar a conexão {0}"},

  	 // SQLConnectionManager.java
  	 {"couldntFindStat", "Não foi possível encontrar a seguinte consulta: {0}."},
  	 {"noInitialContext", "Não foi possível obter um contexto inicial. Tentando usar o PoolMan diretamente."},

  	 // SQLStatement.java
  	 {"invalidParamValue", "O valor para o parâmetro \"{0}\" é inválido para a consulta \"{1}\"."},
  	 {"twoParamSameIndex", "Existem 2 parâmetros com o mesmo índice para a consulta \"{0}\"."},
  	 {"missingParam", "O parâmetro \"{0}\" está faltando na consulta \"{1}\"."},
  	 {"paramWrongType", "O parâmetro {0} possui um tipo incorreto ({1}) para a consulta \"{2}\"."},
  	 {"missingParamStat", "Algum parâmetro está faltando na consulta \"{0}\"."},
  	 {"wrongNumParam", "Número incorreto de parâmetros para a consulta \"{0}\". Necessários: {1}, recebidos: {2}."},
  	 {"name", "Nome: {0}"},
  	 {"invalidFileName", "Nome de arquivo inválido: {0}"},
  	 {"addStatFile", "Adicionando a(s) seguinte(s) consulta(s) do arquivo {0}:"},
  	 {"fileAlreadyParsed", "Arquivo já carregado: {0}"},
  	 {"sqlError", "Foi encontrado um erro ao executar a consulta \"{0}\" com parâmetros \"{1}\"."},
  	 
  	 // DateFormat.java
  	 { "invalidPattern", "O padrão dado é inválido: {0}" },

  	 // DOMUtils
  	 { "attrNotInt",
  	 "O atributo \"{0}\" dentro do elemento \"{1}\" não é um inteiro." },
  	 { "attrNotDefined",
  	 "O atributo \"{0}\" não foi definido dentro do elemento \"{1}\"." },
  	 { "elementNotFound",
  	 "O elemento \"{0}\" não foi encontrado dentro do elemento \"{1}\"." },
  	 {
  		 "NSelementNotFound",
  	 "O elemento \"{0}\" do espaço de nomes \"{1}\" não foi encontrado dentro do elemento \"{2}\"." },

  	 // ContentHandlerManager.java
  	 { "noMoreResults",
  	 "Não é possível adicionar nenhum outro ContentHandler de saída até que o processamento atual termine." },
  	 { "noMoreSources",
  	 "Não é possível gerar nenhum ContentHandler de entrada até que o processamento atual termine." },
  	 { "invalidOutputName", "O nome dado para a saída não pode ser nulo ou vazio." },
  	 { "chNotFound", "O ContentHandler \"{0}\" não foi encontrado." },
  	 { "chNotInList",
  	 "O argumento não está na lista de ContentHandlers para este gerenciador." },
  	 { "chFinishedOutOfOrder",
  	 "ContentHandler de ordem {0} terminou sua execução antes do ContentHandler atual, de ordem {1}." },
  	 { "xmlLocatorNotSet", "XML Document locator não foi tratado devido a uma exceção SAX"} };

}