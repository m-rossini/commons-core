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
  	 {"reloadConnPoolConf", "Recarregando configura��o do Connection Pool Manager..."},
  	 {"done", "Pronto."},
  	 {"addingServer", "Adicionando o servidor � lista de servidores: {0}"},
  	 {"couldntAddServer", "N�o foi poss�vel adicionar o servidor {0} � lista de servidores."},
  	 {"errorConnecting", "Erro ao abrir conex�o com o servidor {0}."},
  	 {"serversDown", "Servidores ca�dos ou inalcan��veis."},
  	 {"problemDisconnectingServer", "Problemas ao desconectar do servidor {0}."},

  	 // ConnectionPoolManager.java
  	 {"closingOpenedServerConns", "Fechando todas as conex�es abertas do servidor..."},
  	 {"problemClosingConnection", "Problemas ao fechar a conex�o {0}"},

  	 // SQLConnectionManager.java
  	 {"couldntFindStat", "N�o foi poss�vel encontrar a seguinte consulta: {0}."},
  	 {"noInitialContext", "N�o foi poss�vel obter um contexto inicial. Tentando usar o PoolMan diretamente."},

  	 // SQLStatement.java
  	 {"invalidParamValue", "O valor para o par�metro \"{0}\" � inv�lido para a consulta \"{1}\"."},
  	 {"twoParamSameIndex", "Existem 2 par�metros com o mesmo �ndice para a consulta \"{0}\"."},
  	 {"missingParam", "O par�metro \"{0}\" est� faltando na consulta \"{1}\"."},
  	 {"paramWrongType", "O par�metro {0} possui um tipo incorreto ({1}) para a consulta \"{2}\"."},
  	 {"missingParamStat", "Algum par�metro est� faltando na consulta \"{0}\"."},
  	 {"wrongNumParam", "N�mero incorreto de par�metros para a consulta \"{0}\". Necess�rios: {1}, recebidos: {2}."},
  	 {"name", "Nome: {0}"},
  	 {"invalidFileName", "Nome de arquivo inv�lido: {0}"},
  	 {"addStatFile", "Adicionando a(s) seguinte(s) consulta(s) do arquivo {0}:"},
  	 {"fileAlreadyParsed", "Arquivo j� carregado: {0}"},
  	 {"sqlError", "Foi encontrado um erro ao executar a consulta \"{0}\" com par�metros \"{1}\"."},
  	 
  	 // DateFormat.java
  	 { "invalidPattern", "O padr�o dado � inv�lido: {0}" },

  	 // DOMUtils
  	 { "attrNotInt",
  	 "O atributo \"{0}\" dentro do elemento \"{1}\" n�o � um inteiro." },
  	 { "attrNotDefined",
  	 "O atributo \"{0}\" n�o foi definido dentro do elemento \"{1}\"." },
  	 { "elementNotFound",
  	 "O elemento \"{0}\" n�o foi encontrado dentro do elemento \"{1}\"." },
  	 {
  		 "NSelementNotFound",
  	 "O elemento \"{0}\" do espa�o de nomes \"{1}\" n�o foi encontrado dentro do elemento \"{2}\"." },

  	 // ContentHandlerManager.java
  	 { "noMoreResults",
  	 "N�o � poss�vel adicionar nenhum outro ContentHandler de sa�da at� que o processamento atual termine." },
  	 { "noMoreSources",
  	 "N�o � poss�vel gerar nenhum ContentHandler de entrada at� que o processamento atual termine." },
  	 { "invalidOutputName", "O nome dado para a sa�da n�o pode ser nulo ou vazio." },
  	 { "chNotFound", "O ContentHandler \"{0}\" n�o foi encontrado." },
  	 { "chNotInList",
  	 "O argumento n�o est� na lista de ContentHandlers para este gerenciador." },
  	 { "chFinishedOutOfOrder",
  	 "ContentHandler de ordem {0} terminou sua execu��o antes do ContentHandler atual, de ordem {1}." },
  	 { "xmlLocatorNotSet", "XML Document locator n�o foi tratado devido a uma exce��o SAX"} };

}