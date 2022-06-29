hljs.registerLanguage("adama", (() => {
    "use strict";

    const IDENT_RE = '[A-Za-z$_][0-9A-Za-z_]*';
    const KEYWORD_LIST = [
        "enum", "dispatch", "record", "message", "channel", "rpc",
        "function", "procedure", "test",
        "import", "view", "bubble", "policy",
        "require", "index", "method",
        "while", "do", "for", "foreach", "if", "break", "continue", "block", "abort", "return",
        "transition", "invoke", "preempt", "assert",
        "iterate", "where", "where_as", "order", "shuffle", "limit", "reduce", "offset", "asc", "desc",
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
    const KEYWORD_MAYBE = {
        className: 'keyword',
        begin: '@maybe'
    };
    const KEYWORD_CONVERT = {
        className: 'keyword',
        begin: '@convert'
    };
    const KEYWORD_STATIC = {
        className: 'keyword',
        begin: '@static'
    };
    const KEYWORD_WEB = {
        className: 'keyword',
        begin: '@web'
    };
    const KEYWORD_WHO = {
        className: 'keyword',
        begin: '@who'
    };
    const KEYWORD_PARAMETERS = {
        className: 'keyword',
        begin: '@parameters'
    };
    const KEYWORD_HEADERS = {
        className: 'keyword',
        begin: '@headers'
    };
    const KEYWORD_CONSTRUCT = {
        className: 'keyword',
        begin: '@construct'
    };
    const KEYWORD_CONNECTED = {
        className: 'keyword',
        begin: '@connected'
    };
    const KEYWORD_DISCONNECTED = {
        className: 'keyword',
        begin: '@disconnected'
    };
    const KEYWORD_ATTACHED = {
        className: 'keyword',
        begin: '@attached'
    };
    const KEYWORD_CAN_ATTACH = {
        className: 'keyword',
        begin: '@can_attach'
    };
    const KEYWORD_DEFAULT = {
        className: 'keyword',
        begin: '@default'
    };
    const KEYWORD_STEP = {
        className: 'keyword',
        begin: '@step'
    };
    const KEYWORD_PUMP = {
        className: 'keyword',
        begin: '@pump'
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
            LITERAL_I,
            LITERAL_NOTHING,
            LITERAL_NULL,
            LITERAL_STABLE,
            LITERAL_BLOCKED,
            LITERAL_STATE_MACHINE_LABEL,
            KEYWORD_MAYBE,
            KEYWORD_CONVERT,
            KEYWORD_STATIC,
            KEYWORD_WEB,
            KEYWORD_WHO,
            KEYWORD_PARAMETERS,
            KEYWORD_HEADERS,
            KEYWORD_CONSTRUCT,
            KEYWORD_CONNECTED,
            KEYWORD_DISCONNECTED,
            KEYWORD_ATTACHED,
            KEYWORD_CAN_ATTACH,
            KEYWORD_DEFAULT,
            KEYWORD_STEP,
            KEYWORD_PUMP,
            e.C_LINE_COMMENT_MODE,
            e.C_BLOCK_COMMENT_MODE,
            e.C_NUMBER_MODE,
            e.QUOTE_STRING_MODE]
    })
})());