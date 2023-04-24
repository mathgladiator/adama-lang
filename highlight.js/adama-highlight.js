hljs.registerLanguage("adama", (() => {
    "use strict";

    const IDENT_RE = '[A-Za-z$_][0-9A-Za-z_]*';
    const KEYWORD_LIST = [
        "enum", "dispatch", "record", "message", "channel", "rpc",
        "service",
        "function", "procedure", "test",
        "import", "view", "bubble", "policy",
        "require", "index", "method",
        "while", "do", "for", "foreach", "if", "break", "continue", "block", "abort", "return",
        "transition", "invoke", "preempt", "assert",
        "iterate", "where", "where_as", "order", "shuffle", "limit", "reduce", "offset", "asc", "desc", "via",
        "create", "send", "invent", "maximum_history", // TODO: scope within @static
        "auto", "var", "let", "formula", "readonly",
        "public", "private", "viewer_is", "use_policy"];


    const LITERAL_NO_ONE = {
        className: 'literal',
        begin: '@no_one'
    };
    const LITERAL_I = {
        className: 'literal',
        begin: '@i'
    };
    const LITERAL_NOTHING = {
        className: 'literal',
        begin: '@nothing'
    };
    const LITERAL_NULL = {
        className: 'literal',
        begin: '@null'
    };
    const LITERAL_STABLE = {
        className: 'literal',
        begin: '@stable'
    };
    const LITERAL_BLOCKED = {
        className: 'literal',
        begin: '@blocked'
    };
    const LITERAL_STATE_MACHINE_LABEL = {
        className: 'keyword',
        begin: '#' + IDENT_RE
    };

    const KEYWORD_AT_PREFIX = {
        className: 'keyword',
        begin: '@' + IDENT_RE
    };

    const LITERALS = [
        "true",
        "false"
    ];

    const BUILT_IN = [
        "bool",
        "int",
        "double",
        "client",
        "asset",
        "dynamic",
        "complex",
        "long",
        "string",
        "label",
        "map",  // <,>
        "table", // <
        "channel", // <
        "list", // <
        "maybe",  // <
        "future" // <
    ];

    const BUILT_IN_VARIABLES = [];

    const KEYWORDS = {
        $pattern: IDENT_RE,
        keyword: KEYWORD_LIST,
        literal: LITERALS,
        built_in: BUILT_IN,
        "variable.language": BUILT_IN_VARIABLES
    };

    return e => ({
        aliases: ["a", "adama"],
        keywords: KEYWORDS,
        contains: [
            LITERAL_NO_ONE,
            LITERAL_I,
            LITERAL_NOTHING,
            LITERAL_NULL,
            LITERAL_STABLE,
            LITERAL_BLOCKED,
            LITERAL_STATE_MACHINE_LABEL,
            KEYWORD_AT_PREFIX,
            e.C_LINE_COMMENT_MODE,
            e.C_BLOCK_COMMENT_MODE,
            e.C_NUMBER_MODE,
            e.QUOTE_STRING_MODE]
    })
})());