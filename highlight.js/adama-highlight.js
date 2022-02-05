hljs.registerLanguage("adama", (() => {
    "use strict";

    const IDENT_RE = '[A-Za-z$_][0-9A-Za-z_]*';
    const KEYWORD_LIST = ["for", "foreach", "var", "let", "formula", "public", "private"];

    const LITERAL_NO_ONE = {
        className: 'literal',
        begin: '@no_one'
    };


    const LITERALS = [
        "true",
        "false",
        "@no_one",
    ];

    const BUILT_IN = [
        "int",
        "double",
    ];

    const BUILT_IN_VARIABLES = [
        "who"
    ];

    const KEYWORDS = {
        $pattern: IDENT_RE,
        keyword: KEYWORD_LIST,
        literal: LITERALS,
        built_in: BUILT_IN,
        "variable.language": BUILT_IN_VARIABLES
    };

    return e => ({
        aliases: ["ad", "adama"],
        keywords: KEYWORDS,
        contains: [
            LITERAL_NO_ONE,
            e.C_LINE_COMMENT_MODE,
            e.C_BLOCK_COMMENT_MODE,
            e.C_NUMBER_MODE,
            e.QUOTE_STRING_MODE]

        // ,
        //             e.HASH_COMMENT_MODE
    })
})());