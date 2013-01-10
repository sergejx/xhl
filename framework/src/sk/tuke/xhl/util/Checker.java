/*
 * XHL - Extensible Host Language
 * Copyright 2013 Sergej Chodarev
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package sk.tuke.xhl.util;

import sk.tuke.xhl.core.Error;
import sk.tuke.xhl.core.*;
import sk.tuke.xhl.core.elements.Block;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

/**
 * Checker is a command line program that allows to check source codes of the
 * XHL program according to XHL grammar and language schema.
 *
 * @author Sergej Chodarev
 */
public class Checker {


    public static void main(String[] args) {
        if (args.length < 1) {
            System.out.println("No input files specified\n" +
                    "Usage: java Checker <file-name> [<language>]");
            System.exit(1);
        }

        File input = new File(args[0]);
        String language = null;
        if (args.length >= 2)
            language = args[1];

        try {
            check(input, language);
        } catch (FileNotFoundException e) {
            System.err.println("File '" + args[0] + "' not found");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("I/O error while reading file '" + args[0] + "'");
            System.exit(1);
        }
    }

    private static void check(File input, String language) throws
            IOException {
        MaybeError<Block> result = Reader.read(input);
        if (!result.succeed()) {
            printErrors(result.getErrors());
        } else {
            ModulesProvider.ModulesLoader loader = new ModulesProvider
                    .ModulesLoader();
            Module module = loader.loadModule(language);
            if (module == null) {
                System.err.print("Unknown language '" + language + "'");
                System.exit(1);
            }
            LanguageProcessor processor = new LanguageProcessor(module);
            List<Error> errors = processor.validate(result.get());
            printErrors(errors);
        }
    }

    private static void printErrors(List<Error> errors) {
        for (Error error : errors) {
            System.out.println(error.position + ": " + error.message);
        }
    }
}
