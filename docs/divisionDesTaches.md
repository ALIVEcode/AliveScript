###Precompile time

1. retirer les commentaires
2. retirer les commentaires multilignes
3. join ensemble les lignes finissant en \\ et les lignes d'après
   - exemple: <code>afficher \  
            "bonjour"  </code>
            devient <code>afficher "bonjour"</code>



###Compile time

1. supprime l'ancien code compilé s'il existe
2. pour chaque ligne:
   1. (Lexing) transforme la ligne en liste de token *List\<Token>*
   2. (Parsing) transforme la liste de token en programme *Programme*
3. déclare les variables qui seront utilisées au runtime
4. lance des erreurs de variables si elles sont mal déclarées
    - exemple: si des constantes sont déclarées à plusieurs reprises
5. lance des erreurs de fermeture si certains blocs sont mal fermés



### Runtime

1. 