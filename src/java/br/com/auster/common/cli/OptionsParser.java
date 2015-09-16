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

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.cli.UnrecognizedOptionException;

/**
 * @author Ricardo Barone
 * @version $Id: OptionsParser.java 91 2005-04-07 21:13:55Z framos $
 */
public class OptionsParser
{

   public static final char LANGUAGE_OPT = 'l';
   public static final char COUNTRY_OPT = 'c';
   public static final char HELP_OPT = 'h';
   public static final char VERSION_OPT = 'v';

   public static final String LANGUAGE_LONG_OPT = "language";
   public static final String COUNTRY_LONG_OPT = "country";
   public static final String HELP_LONG_OPT = "help";
   public static final String VERSION_LONG_OPT = "version";

   protected static final Map optionsValues = new HashMap();

   protected Options options;
   protected String className;
   protected String title;
   protected CLOption[] params;
   protected char helpOpt = '\0', versionOpt = '\0', langOpt = '\0', countryOpt = '\0';

   /**
    * Creates an OptionsParser object, that is used to parse the command line
    * arguments.
    * 
    * The help and version options will be automatically created if the
    * <code>mainClass</code> and the <code>versionTitle</code> are not null,
    * respectivelly.
    * 
    * The short option for help will be 'h' and the long 'help'. The short
    * option for version will be 'v' and the long 'version'.
    * 
    * @param params
    *           Array that contains the possible options that this instance will
    *           take care.
    * @param mainClass
    *           The class that has the <code>main</code> method, used for
    *           print the usage in help message. Use null for no help option
    *           pre-defined.
    * @param versionTitle
    *           String defining the version message (usually the program
    *           version). Use null for no version option pre-defined.
    * @param useDefaultLocaleOptions
    *           If true, the options for locale will be added to the informed
    *           parameters. The short option 'l' (long = 'language') will be
    *           used for language definition and the short option 'c' (long =
    *           'country') will be used for country definition.
    * @throws IllegalArgumentException
    *            if two or more options has the same short or long option.
    */
   public OptionsParser(CLOption[] params,
                        Class mainClass,
                        String versionTitle,
                        boolean useDefaultLocaleOptions)
   {
      this(
            params,
            mainClass != null ? getDefaultHelpOption("Prints this message and exits.")
                  : null,
            mainClass,
            versionTitle != null ? getDefaultVersionOption("Prints the system version and exits.")
                  : null,
            versionTitle,
            useDefaultLocaleOptions ? getDefaultLangOption("Specifies the language for system messages.")
                  : null,
            useDefaultLocaleOptions ? getDefaultCountryOption("Specifies the country for system messages.")
                  : null);
   }

   /**
    * Creates an OptionsParser object, that is used to parse the command line
    * arguments, build the help message and print the version message, if
    * requested.
    * 
    * @param params
    *           Array that contains the possible options that this instance will
    *           take care.
    * @param helpOption
    *           If this option is found, the help message will be shown. Use
    *           null for no help message
    * @param mainClass
    *           The class that has the <code>main</code> method, used for
    *           print the usage in help message.
    * @param versionOption
    *           If this option is found, the version message will be shown. Use
    *           null for no version message.
    * @param versionTitle
    *           String defining the version message (usually the program
    *           version).
    * @param langOption
    *           If this option is found, the language (locale) for the JVM will
    *           be changed. Use null for no language option.
    * @param countryOption
    *           If this option is found, the country (locale) for the JVM will
    *           be changed. Use null for no country option.
    * @throws IllegalArgumentException
    *            if two or more options has the same short or long option.
    */
   public OptionsParser(CLOption[] params,
                        CLOption helpOption,
                        Class mainClass,
                        CLOption versionOption,
                        String versionTitle,
                        CLOption langOption,
                        CLOption countryOption)
   {
      if(params == null) params = new CLOption[0];

      this.options = new Options();
      this.params = params;
      this.className = mainClass != null ? mainClass.getName() : "";
      this.title = versionTitle != null ? versionTitle : "";

      // group all the user options in the Option object
      for(int i = 0; i < params.length; i++)
         addOption(params[i]);

      // Adds the help and version options, if defined.
      if(helpOption != null)
      {
         this.helpOpt = helpOption.getShortOption();
         addOption(helpOption);
      }
      if(versionOption != null)
      {
         this.versionOpt = versionOption.getShortOption();
         addOption(versionOption);
      }
      // Adds the language (locale) options.
      if(langOption != null)
      {
         this.langOpt = langOption.getShortOption();
         addOption(langOption);
      }
      if(countryOption != null)
      {
         this.countryOpt = countryOption.getShortOption();
         addOption(countryOption);
      }
   }

   /**
    * Adds the given option to the acceptable options of this instance.
    * 
    * @param option
    *           the option to be added.
    * @throws IllegalArgumentException
    *            if two or more options has the same short or long option.
    */
   protected final void addOption(CLOption option)
   {
      if(option == null) return;

      if(this.options.hasOption(option.getLongOption())
            || this.options.hasOption(Character.toString(option.getShortOption())))
            throw new IllegalArgumentException(
                  "Trying to add a different option that is already added: "
                        + option.getLongOption());

      this.options.addOption(option.getOption());
   }

   /**
    * Gets a default help option. The short option will be 'h' and the long
    * 'help'.
    * 
    * @param helpOptionDesc
    *           The description for the help option (shown in the help message).
    *           Use null for a default message.
    * @return a CLOption instance for the help option.
    */
   public static final CLOption getDefaultHelpOption(String helpOptionDesc)
   {
      return new CLOption(HELP_LONG_OPT, HELP_OPT, false, false, null, helpOptionDesc);
   }

   /**
    * Gets a default version option. The short option will be 'v' and the long
    * 'version'.
    * 
    * @param versionOptionDesc
    *           The description for the version option (shown in the help
    *           message). Use null for a default message.
    * @return a CLOption instance for the version option.
    */
   public static final CLOption getDefaultVersionOption(String versionOptionDesc)
   {
      return new CLOption(VERSION_LONG_OPT, VERSION_OPT, false, false, null,
            versionOptionDesc);
   }

   /**
    * Gets a default language option. The short option will be 'l' and the long
    * 'language'.
    * 
    * @param langOptionDesc
    *           The description for the language option (shown in the help
    *           message). Use null for a default message.
    * @return a CLOption instance for the language option.
    */
   public static final CLOption getDefaultLangOption(String langOptionDesc)
   {
      return new CLOption(LANGUAGE_LONG_OPT, LANGUAGE_OPT, false, true, "language",
            langOptionDesc);
   }

   /**
    * Gets a default country option. The short option will be 'c' and the long
    * 'country'.
    * 
    * @param countryOptionDesc
    *           The description for the country option (shown in the help
    *           message). Use null for a default message.
    * @return a CLOption instance for the country option.
    */
   public static final CLOption getDefaultCountryOption(String countryOptionDesc)
   {
      return new CLOption(COUNTRY_LONG_OPT, COUNTRY_OPT, false, true, "country",
            countryOptionDesc);
   }

   /**
    * Parses the command line arguments, using the user defined options in this
    * object.
    * 
    * @param args
    *           Command line arguments.
    */
   public void parse(String[] args)
   {
      try
      {
         // Parse the command line
         CommandLine cmd = new PosixParser().parse(this.options, args);

         // version and help options have higher priority
         if(cmd.hasOption(this.versionOpt))
         {
            System.out.println(title);
            System.exit(0);
         }
         if(cmd.hasOption(this.helpOpt)) printHelp(null);

         // Gets the language options and sets the new system locale
         setLocale(cmd);

         // Let's check if the required parameters were given
         checkRequiredParams(cmd);

         // here we set all the parameters values in the property object
         setOptionsValues(cmd);

      }
      catch(MissingArgumentException e)
      {
         // print the missing arguments for a option which requires one and exit
         printMissingArgs(e.getMessage());

      }
      catch(UnrecognizedOptionException e)
      {
         // we display the first unrecognized option and exit
         printHelp("Unrecognized options");

      }
      catch(org.apache.commons.cli.ParseException e)
      {
         printHelp("Unkown error: " + e);
      }
   }

   /**
    * Reads the language seausterng from the command line and set the default
    * JVM locale.
    * 
    * @param cmd
    *           ComandLine object which contains the parsed arguments.
    */
   protected void setLocale(CommandLine cmd)
   {
      // Sets language and country
      if(cmd.hasOption(this.langOpt))
      {
         Locale locale;
         if(cmd.hasOption(this.countryOpt))
            locale = new Locale(cmd.getOptionValue(this.langOpt),
                  cmd.getOptionValue(this.countryOpt));
         else
            locale = new Locale(cmd.getOptionValue(this.langOpt));
         Locale.setDefault(locale);
      }
   }

   /**
    * Checks the required parameters.
    */
   protected void checkRequiredParams(CommandLine cmd)
   {
      for(int i = 0; i < this.params.length; i++)
      {
         CLOption param = this.params[i];

         if(param.isRequired() && !cmd.hasOption(param.getShortOption()))
         {
            printHelp("The following option is required:"
                  + System.getProperty("line.separator") + " - " + param.getLongOption());
         }
      }
   }

   /**
    * Put the command line options and its values in a static hash, so the
    * static methods may access them.
    */
   protected void setOptionsValues(CommandLine cmd)
   {
      Option[] allOptions = cmd.getOptions();

      for(int i = 0; i < allOptions.length; i++)
      {
         String option = allOptions[i].getLongOpt();
         String value = cmd.getOptionValue(allOptions[i].getOpt());

         synchronized(optionsValues)
         {
            if(value != null)
               optionsValues.put(option, value);
            else
               optionsValues.put(option, "");
         }
      }
   }

   /****************************************************************************
    * Retrieve the argument, if any, of an option.
    * 
    * @param option
    *           name of the option.
    * @return Value of the argument if option is set and has an argument. Empty
    *         string if there is no value associated with it. Null if the option
    *         was not specified in the command line.
    */
   public static final String getOptionValue(String option)
   {
      return (String) optionsValues.get(option);
   }

   /****************************************************************************
    * Retrieve the argument, if any, of an option.
    * 
    * @param option
    *           name of the option.
    * @param defaultValue
    *           is the default value to be returned if the option is not
    *           specified.
    * @return Value of the argument if option is set and has an argument. Empty
    *         string if there is no value associated with it.
    *         <code>default</code> if the option was not specified in the
    *         command line.
    */
   public static final String getOptionValue(String option, String defaultValue)
   {
      String value = (String) optionsValues.get(option);
      if(value == null)
         return defaultValue;
      else
         return value;
   }

   /**
    * Gets all the entries (long options) in the internal options structure.
    * 
    * @return a String array containing all the options specified for any
    *         instances of this class.
    */
   public static final String[] getOptions()
   {
      return (String[]) optionsValues.keySet().toArray(new String[0]);
   }

   /**
    * Print missing argument, appending to the error message from the exception.
    * We display the respective long option which has missing arguments, and the
    * help message with all options.
    * 
    * @param errorMessage
    *           The error message that contains the option that has a missing
    *           argument.
    */
   protected void printMissingArgs(String errorMessage)
   {
      String[] missingArgs = errorMessage.split(":");
      String lineSeparator = System.getProperty("line.separator");

      String errMessage = "The required argument for the following option is missing: "
            + lineSeparator + "- " + options.getOption(missingArgs[1]).getLongOpt();

      printHelp(errMessage);
   }

   /**
    * Prints a error message (if not null) and the help message.
    * 
    * @param errorMessage
    *           if null, no error message will be shown and the exit code will
    *           be 1. Otherwise the error message will be shown and the exit
    *           code will be 0.
    */
   public void printHelp(String errorMessage)
   {
      HelpFormatter formatter = new HelpFormatter();
      String header = (errorMessage != null) ? "Error: " + errorMessage : "";

      formatter.printHelp("<program> [options]", header, options, "", true);
      System.exit(errorMessage != null ? 1 : 0);
   }
}