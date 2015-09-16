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
package br.com.auster.common.cli;

import org.apache.commons.cli.Option;
import org.apache.commons.cli.OptionBuilder;

/**
 * Represents a command line option.
 * 
 * @author Ricardo Barone
 * @version $Id: CLOption.java 91 2005-04-07 21:13:55Z framos $
 */
public class CLOption
{

   protected String longOption;
   protected char shortOption;
   protected boolean isRequired;
   protected boolean isArgRequired;
   protected String argumentName;
   protected String description;

   /****************************************************************************
    * CLOption constructor.
    * 
    * Format: (parameter name (and long option), short name, is required?, arg
    * is required?, argument name, description).
    */
   public CLOption(String longOption,
                 char shortOption,
                 boolean isRequired,
                 boolean isArgRequired,
                 String argumentName,
                 String description)
   {
      this.longOption = longOption;
      this.shortOption = shortOption;
      this.isRequired = isRequired;
      this.isArgRequired = isArgRequired;
      this.argumentName = argumentName;
      this.description = description;
   }

   public String getLongOption()
   {
      return longOption;
   }

   public char getShortOption()
   {
      return shortOption;
   }

   public boolean isRequired()
   {
      return isRequired;
   }

   public boolean isArgRequired()
   {
      return isArgRequired;
   }

   public String getArgumentName()
   {
      return argumentName;
   }

   public String getDescription()
   {
      return description;
   }

   protected Option getOption()
   {
      OptionBuilder optionBuilder;

      // if the option is required, it will be checked in the
      // OptionsParser constructor to avoid handling exceptions and
      // help/version messages.
      optionBuilder = OptionBuilder.withDescription(description).withLongOpt(longOption).hasArg(isArgRequired);

      if(argumentName != null)
      {
         optionBuilder.withArgName(argumentName);
      }

      return optionBuilder.create(shortOption);
   }
}