/*
* Adama Platform and Language
* Copyright (C) 2021 - 2023 by Adama Platform Initiative, LLC
* 
* This program is free software: you can redistribute it and/or modify
* it under the terms of the GNU Affero General Public License as published
* by the Free Software Foundation, either version 3 of the License, or
* (at your option) any later version.
* 
* This program is distributed in the hope that it will be useful,
* but WITHOUT ANY WARRANTY; without even the implied warranty of
* MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
* GNU Affero General Public License for more details.
* 
* You should have received a copy of the GNU Affero General Public License
* along with this program.  If not, see <https://www.gnu.org/licenses/>.
*/
package org.adamalang;

/** centralized listing of all error codes */
public class ErrorCodes {
  public static final int IMPOSSIBLE = 101386;

  @RetryInternally
  public static final int DURABLE_LIVING_DOCUMENT_STAGE_FRESH_DRIVE = 123392;

  @RetryInternally
  public static final int DURABLE_LIVING_DOCUMENT_STAGE_FRESH_PERSIST = 198657;

  @RetryInternally
  public static final int DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_DRIVE = 130568;

  @RetryInternally
  public static final int DURABLE_LIVING_DOCUMENT_STAGE_CONSTRUCT_PERSIST = 134152;

  @RetryInternally
  public static final int DURABLE_LIVING_DOCUMENT_STAGE_LOAD_DRIVE = 143880;

  @RetryInternally
  public static final int DURABLE_LIVING_DOCUMENT_STAGE_ATTACH_PRIVATE_VIEW = 138255;
  public static final int DURABLE_LIVING_DOCUMENT_FAILURE_CREATE_PRIVATE_VIEW = 184319;

  public static final int LIVING_DOCUMENT_TRANSACTION_UNKNOWN_EXCEPTION = 180978;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_COMMAND_FOUND = 194575;
  public static final int LIVING_DOCUMENT_TRANSACTION_UNRECOGNIZED_FIELD_PRESENT = 184335;

  @User
  @Description("The message given to the channel was not parsable")
  public static final int LIVING_DOCUMENT_TRANSACTION_FAILED_PARSE_MESSAGE = 145627;
  public static final int LIVING_DOCUMENT_TRANSACTION_ALREADY_CONNECTED = 115724;

  @NotProblem
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_CHANGE = 109775;
  public static final int LIVING_DOCUMENT_FAILURE_LOAD = 139469;
  public static final int LIVING_DOCUMENT_TRANSACTION_ALREADY_CONSTRUCTED = 132111;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_DISCONNECT_DUE_TO_NOT_CONNECTED = 145423;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CHANNEL = 160268;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_MESSAGE = 184332;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NO_CONTEXT = 127155;

  @User
  @Description("The user was not connected nor was the message handler not open.")
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SEND_NOT_CONNECTED = 143373;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_ATTACH_NOT_CONNECTED = 125966;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SET_PASSWORD_NO_CONTEXT = 197852;
  public static final int LIVING_DOCUMENT_TRANSACTION_CANT_SET_PASSWORD_NO_PASSWORD = 199907;

  @User
  @Description("The document rejected the connection due to the @connected handler returning false.")
  public static final int LIVING_DOCUMENT_TRANSACTION_CLIENT_REJECTED = 184333;

  @User
  @Description("The document refused to be deleted due to the document's @delete handler")
  public static final int LIVING_DOCUMENT_TRANSACTION_DELETE_REJECTED = 147186;

  public static final int LIVING_DOCUMENT_TRANSACTION_NO_CLIENT_AS_WHO = 122896;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_PUT = 129727;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_DELETE = 121027;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_LIMIT = 146448;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_DELIVERY_ID = 187586;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_RESULT = 127169;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_ROUTE_DOCUMENT = 197827;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_ROUTE_CACHE = 168131;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_CONSTRUCTOR_ARG = 196624;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_TIMESTAMP = 143889;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_VALID_COMMAND_FOUND = 132116;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_ASSET = 143380;
  public static final int LIVING_DOCUMENT_TRANSACTION_NO_PATCH = 193055;
  public static final int LIVING_DOCUMENT_TRANSACTION_MESSAGE_ALREADY_SENT = 143407;

  @User
  @Description("The message handler was aborted")
  public static final int LIVING_DOCUMENT_TRANSACTION_MESSAGE_DIRECT_ABORT = 127152;

  @User
  @Description("The message handler was aborted due to a privacy policy")
  public static final int LIVING_DOCUMENT_TRANSACTION_MESSAGE_DIRECT_ABORT_POLICY = 130559;

  public static final int LIVING_DOCUMENT_TRANSACTION_EXPIRE_LIMIT_MUST_BE_POSITIVE = 122412;
  public static final int LIVING_DOCUMENT_TRANSACTION_EXPIRE_DID_NOTHING = 131203;
  public static final int FACTORY_CANT_BIND_JAVA_CODE = 198174;
  public static final int FACTORY_CANT_COMPILE_JAVA_CODE = 180258;
  public static final int FACTORY_CANT_CREATE_OBJECT_DUE_TO_CATASTROPHE = 115747;
  public static final int FACTORY_CANT_INVOKE_CAN_CREATE = 180858;
  public static final int FACTORY_CANT_INVOKE_CAN_SEND_WHILE_DISCONNECTED = 148095;
  public static final int FACTORY_CANT_INVOKE_CAN_INVENT = 146558;

  public static final int CORE_DELAY_ADAMA_STREAM_TIMEOUT = 116936;
  public static final int CORE_DELAY_ADAMA_STREAM_REJECTED = 115917;

  @RetryInternally
  public static final int CATASTROPHIC_DOCUMENT_FAILURE_EXCEPTION = 144416;

  @RetryInternally
  public static final int SHIELD_REJECT_NEW_DOCUMENT = 146631;
  @RetryInternally
  public static final int SHIELD_REJECT_CONNECT_DOCUMENT = 183498;
  @RetryInternally
  public static final int SHIELD_REJECT_SEND_MESSAGE = 199883;

  public static final int DOCUMENT_SHEDDING_LOAD = 146115;
  public static final int DOCUMENT_DRAINING_LOAD = 193265;

  @User
  @Description("The web get handler was not found.")
  public static final int DOCUMENT_WEB_GET_NOT_FOUND = 133308;
  public static final int DOCUMENT_WEB_GET_CANCEL = 128208;

  @User
  @Description("The web get was aborted.")
  public static final int DOCUMENT_WEB_GET_ABORT = 145659;

  @User
  @Description("The web options handler was not found")
  public static final int DOCUMENT_WEB_OPTIONS_NOT_FOUND = 127692;

  @User
  @Description("The web put handler was not found.")
  public static final int DOCUMENT_WEB_PUT_NOT_FOUND = 114881;

  @User
  @Description("The web put was aborted.")
  public static final int DOCUMENT_WEB_PUT_ABORT = 134399;

  @User
  @Description("The web delete handler was not found.")
  public static final int DOCUMENT_WEB_DELETE_NOT_FOUND = 110832;

  @User
  @Description("The web delete was aborted by the handler")
  public static final int DOCUMENT_WEB_DELETE_ABORT = 120060;

  @User
  @Description("The authorization to the document failed")
  public static final int DOCUMENT_AUTHORIIZE_FAILURE = 191713;

  @User
  @Description("The document is already created, so please try connecting instead. This happens when two create calls are executed.")
  public static final int SERVICE_DOCUMENT_ALREADY_CREATED = 130092;

  @User
  @Description("The document was rejected because the @static { create {...} } policy returned false. Either your are not allowed to create the document, or the create() call doesn't exist or is buggy.")
  public static final int SERVICE_DOCUMENT_REJECTED_CREATION = 134259;

  public static final int DOCUMENT_SELF_DESTRUCT_SUCCESSFUL = 134195;

  public static final int INMEMORY_DATA_PATCH_CANT_FIND_DOCUMENT = 144944;
  public static final int INMEMORY_DATA_COMPUTE_CANT_FIND_DOCUMENT = 106546;
  public static final int INMEMORY_DATA_COMPUTE_PATCH_NOTHING_TODO = 120944;
  public static final int INMEMORY_DATA_COMPUTE_REWIND_NOTHING_TODO = 128052;
  public static final int INMEMORY_DATA_DELETE_CANT_FIND_DOCUMENT = 117816;
  public static final int INMEMORY_DATA_COMPUTE_INVALID_METHOD = 127034;
  public static final int INMEMORY_DATA_COMPACT_CANT_FIND_DOCUMENT = 103060;
  public static final int DOCUMENT_QUEUE_BUSY_TOO_MANY_PENDING_ITEMS = 123004;
  public static final int DOCUMENT_QUEUE_BUSY_WAY_BEHIND = 192639;
  public static final int DOCUMENT_QUEUE_CONFLICT_OPERATIONS = 159869;
  public static final int CORE_STREAM_CAN_ATTACH_UNKNOWN_EXCEPTION = 146569;
  public static final int DOCUMENT_NOT_READY = 194752;
  public static final int UNCAUGHT_EXCEPTION_WEB_SOCKET = 295116;
  public static final int ONLY_ACCEPTS_TEXT_FRAMES = 213711;
  public static final int USERLAND_REQUEST_NO_METHOD_PROPERTY = 213708;
  public static final int USERLAND_REQUEST_NO_ID_PROPERTY = 233120;
  public static final int ASSET_KEY_WRONG_LENGTH = 144583;

  /**
   * all DataService implementations must use this for a patch failure due to sequencer out of whack
   */
  public static final int UNIVERSAL_PATCH_FAILURE_HEAD_SEQ_OFF = 621580;

  @User
  @Description("The given document was not found")
  public static final int UNIVERSAL_LOOKUP_FAILED = 625676;

  @User
  @Description("The given document already exists")
  public static final int UNIVERSAL_INITIALIZE_FAILURE = 667658;

  public static final int DEPLOYMENT_NOT_FOUND = 643084;

  @User
  @Description("The requested space already exists.")
  public static final int FRONTEND_SPACE_ALREADY_EXISTS = 679948;

  @User
  @Description("The request space does not exist.")
  public static final int FRONTEND_SPACE_DOESNT_EXIST = 625678;

  @User
  @Description("The requested space does not exist, or the plan is not set")
  public static final int FRONTEND_PLAN_DOESNT_EXIST = 609294;

  @User
  @Description("The requested space does not exist, or the plan is not set")
  public static final int FRONTEND_RXHTML_DOESNT_EXIST = 656403;

  public static final int FRONTEND_INTERNAL_PLAN_DOESNT_EXIST = 654341;
  public static final int INVALID_ROLE = 688141;
  public static final int FRONTEND_AUTHORITY_ALREADY_EXISTS = 601088;
  public static final int FRONTEND_AUTHORITY_SET_NOT_FOUND_OR_WRONG_OWNER = 634880;
  public static final int FRONTEND_AUTHORITY_CHANGE_OWNER_NOT_FOUND_OR_INCORRECT = 662528;
  public static final int FRONTEND_AUTHORITY_GET_NOT_FOUND_INTERNAL = 643072;
  public static final int FRONTEND_AUTHORITY_GET_NOT_FOUND_PUBLIC = 626691;
  public static final int FRONTEND_AUTHORITY_DELETE_NOT_FOUND_OR_INCORRECT = 654339;
  public static final int DELIVERER_FAILURE_NOT_SET = 161987;

  @User
  @Description("The deployment plan must be a object")
  public static final int DEPLOYMENT_PLAN_MUST_BE_ROOT_OBJECT = 117818;

  @User
  @Description("The deployment plan lacked a versions object within the root object")
  public static final int DEPLOYMENT_PLAN_NO_VERSIONS = 115788;

  @User
  @Description("The deployment plan lacked a default version")
  public static final int DEPLOYMENT_PLAN_NO_DEFAULT = 143948;

  @User
  @Description("The deployment plan is not correct; the versions field must be an object")
  public static final int DEPLOYMENT_PLAN_VERSIONS_MUST_BE_OBJECT = 155711;

  @User
  @Description("The deployment plan is not correct; the plan field be an array")
  public static final int DEPLOYMENT_PLAN_PLAN_MUST_BE_ARRAY = 126012;

  @User
  @Description("The deployment plan is not correct; elements with the plan array must be objects")
  public static final int DEPLOYMENT_PLAN_PLAN_ARRAY_ELEMENT_MUST_OBJECT = 176703;

  @User
  @Description("The deployment plan is not correct; plan object must point to a version which exists")
  public static final int DEPLOYMENT_PLAN_VERSION_MUST_EXIST = 120895;

  @User
  @Description("The deployment plan is not correct; plan object must have a version")
  public static final int DEPLOYMENT_PLAN_PLAN_NO_VERSION = 199768;

  @User
  @Description("The deployment plan is not correct; default version doesn't exist")
  public static final int DEPLOYMENT_PLAN_MUST_HAVE_DEFAULT = 145980;

  @User
  @Description("The deployment plan is not correct; the keys within a plan object must be an array")
  public static final int DEPLOYMENT_PLAN_KEYS_MUST_BE_ARRAY = 199886;

  @User
  @Description("The given Adama file was unable to be parsed; see the associated errorJson()")
  public static final int DEPLOYMENT_CANT_PARSE_LANGUAGE = 117823;

  @User
  @Description("The given Adama file was unable to be typed; see associated errorJson()")
  public static final int DEPLOYMENT_CANT_TYPE_LANGUAGE = 132157;

  @User
  @Description("The space was not found on the given Adama host. Either this means the space doesn't exist, or a routing issue caused by a poor deployment or capacity management resulted in a invalid mapping")
  public static final int DEPLOYMENT_FACTORY_CANT_FIND_SPACE = 134214;

  @User
  @Description("The deployment plan contains junk")
  public static final int DEPLOYMENT_UNKNOWN_FIELD_ROOT = 143430;

  @User
  @Description("The deployment plan contains junk within a plan object")
  public static final int DEPLOYMENT_UNKNOWN_FIELD_STAGE = 116812;

  @User
  @Description("The deployment plan failed for an unknown reason")
  public static final int DEPLOYMENT_UNKNOWN_EXCEPTION = 146561;

  @Description("The query did make sense")
  public static final int QUERY_MADE_NO_SENSE = 130242;

  public static final int DOMAIN_LOOKUP_FAILURE = 177395;
  public static final int DOMAIN_TRANSLATE_FAILURE = 148477;
  public static final int FRONTEND_FAILED_RXHTML_LOOKUP = 120048;

  public static final int TASK_CANCELLED = 114384;

  public static final int PLAN_FETCH_LOOKUP_FAILURE = 133371;
  public static final int CAPACITY_FETCH_LOOKUP_FAILURE = 131825;
  public static final int ASYNC_COMPLEX_DEPLOYMENT_FAILURE = 127736;
  public static final int CACHED_BYTE_CODE_FAILED_PACK = 162036;
  public static final int CACHED_BYTE_CODE_FAILED_UNPACK = 127732;

  /**
   * 193267 193264 111347 197872
   * 184063 125180 123644 130303 136444 165117 127745 182531 149251 148736 130819 100100 118020
   * 135940 131845 195851 131855 134927 122124 101135 135948 196876 130833 197905 195871 131356
   * 198434 144161 127780 131879 180004 134954 118573 134958 182578 101680 195891 103219 199472
   * 127794 148279 114484 199995 140088 143166 180031 142143 171839 115518 107328 131904 197447
   * 196939 196936 131919 183116 196448 110953 195951 123760 160115 145267 138096 131964 111487
   * 199043 115584 134016 115592 118669 116623 196492 180109 114582 135070 147363 144288 197548
   * 146354 163763 149427 114608 118707 147376 184258 163778 138179 142787 144320 180160 163776
   * 133056 127938 146881 177095 146887 143818 196555 193481 117709 151503 111564 118735 134099
   * 150483 151504 184280 188383 165852 118752 151011 123875 139745 183781 198639 189423 129007
   * 194547 193011 189424 133105 130040 193534 118781 142847 119804 167423 133119 131068
   */

  public static final int USER_NOT_FOUND_GET_PASSWORD = 684039;
  public static final int USER_NOT_FOUND_GET_BALANCE = 605208;
  public static final int USER_FAILED_TO_COUNT = 662552;
  public static final int PRIVATE_KEY_NOT_FOUND = 643100;
  public static final int USER_NOT_FOUND_GET_PROFILE = 674832;
  public static final int USER_FAILED_TO_SET_PROFILE = 634899;
  public static final int USER_NOT_FOUND = 604191;

  public static final int DOMAIN_LOOKUP_WEB_NULL_FAILURE = 642079;
  public static final int DOMAIN_LOOKUP_WEB_NO_KEY_FAILURE = 647199;
  public static final int WEB_FAILED_ASSET_UPLOAD_ALL = 695327;

  /**
   * 634900 643095 605227 639018 668719 684079 602158 642094 622624 605216 618532 605223 687163
   * 640056 654392 656443 639034 654394 684089 662591 603196 650300 666687 629822 691261 652350
   * 684082 620592 601136 658483 605235 678960 688176 635955 605234 651316 625716 688180 647244
   * 649292 606284 638028 691279 639052 655436 630851 605251 658500 639059 629868 639072 654459
   * 640124 653436 605311 656499 659568 622707 629900 671887 620687 665740 605327 602255 667789
   * 639105 670852 651416 655504 642235 654523 651453 622768 659632 653490 640183 646344 606411
   * 646347 654539 602317 641229 654540 655567 671951 601292 697548 681164 606401 603329 622787
   * 626883 602307 608450 642245 602308 688327 639175 651463 651462 662725 687323 652507 613596
   * 667859 645328 670928 688336 691408 658647 691412 687338 603368 688363 651499 687342 649452
   * 622828 667884 605422 651489 653536 657635 691424 659680 602339 604386 684263 655610 658682
   * 657658 688376 618749 625917 667903 651516 688383 654590 652542 616689 689394 644337 602353
   * 685299 669939 668915 680179 645360 670960 662768 675056 646386 696561 653554 670966 642292
   * 618743 652535 635126 642313 654600 675083 609548 670991 667916 691468 602382 657677 642311
   * 641311 675091 605484 625964 606511 675123 657712 639282 653620 651592 639311 615744 605506
   * 671091 666992 688496 668044 655746 675203 642439 639391 630174 627104 604576 643519 652735
   * 606640 672176 655792 658890 602568 640461 650701 656847 667072 640451 602562 655839 638428
   * 658900 658927 654831 695779 668154 647676 699888 651763 651762 642548 654861 641549 667151
   * 606735 647695 642575 658957 688642 635392 676355 646659 639491 623107 669185 671238 642576
   * 655912 604716 640559 658983 695844 639544 602683 655935 667199 606780 687676 684604 650814
   * 643633 691762 625200 672307 638515 685616 655927 654903 699993 606844 659087 669324 638592
   * 651907 602800 639666 671438 624332 641743 672460 656064 621251 668352 656065 672455 639696
   * 635628 656108 640743 611065 610040 656124 668413 667379 606963 688880 652018 672500 602889
   * 671498 639752 644872 684808 603915 668424 642826 684814 654093 664335 687887 660239 659212
   * 604943 654095 622351 639745 638720 673539 605952 626435 616194 668417 667396 646919 623384
   * 652059 671519 643868 652049 659218 663315 646931 666384 684816 652051 658193 642861 638765
   * 662319 641836 646956 688940 605999 652078 671522 655136 641824 660256 622371 639783 655161
   * 675640 647997 618300 660287 610111 607039 603953 623409 664370 626480 670515 694067 608048
   * 663344 668464 610099 658224 649011 684854 680759 603956 639799 602935 606006 675637 655176
   * 671567 602956 680783 646976 606016 651075 659283 655213 668526 601967 644988 646012 639871
   * 626559 652145 623472 648051 643954 655240 642955 647055 641934 654208 651136 623491 662400
   * 606083 656260 642968 675744 603064 689083 697279 675775 641983 691123 689075 663475 642996
   * 664503 622537 671691 601035 660430 638925 695246 628684 656332 623567 652239 653263 651215
   * 670668 608207 667597 640974 639937 619456 690115 669635 662467 624579 623555 692160 648130
   * 630722 652231 673732 693188 603096 654300 648159 625631 638942 655314 689108 651240 672751
   * 663532 638959 626670 659427 603107 635879 619512 668665 643066 623613 605180 669695 696316
   * 656380 635903 625663 684029 653310 652286 605182 692221 654321 641008 685043 606195 626675
   * 698352 641010 635893 605172 691188
   */
  public static final int AUTH_FAILED_DOC_AUTHENTICATE = 982253;
  public static final int AUTH_FAILED_VALIDATING_AGAINST_KEYSTORE = 916531;
  public static final int AUTH_FORBIDDEN = 403403;
  public static final int AUTH_DISCONNECTED = 403500;
  public static final int AUTH_INVALID_TOKEN_LAYOUT = 995342;
  public static final int AUTH_INVALID_TOKEN_JSON = 908303;
  public static final int AUTH_INVALID_TOKEN_JSON_COMPLETE = 959500;
  public static final int USERID_RESOLVE_UNKNOWN_EXCEPTION = 979980;
  public static final int API_INVALID_EMAIL = 905293;
  public static final int SPACE_POLICY_LOCATOR_UNKNOWN_EXCEPTION = 969741;
  public static final int DOMAIN_RESOLVE_UNKNOWN_EXCEPTION = 942318;

  public static final int API_SPACE_CREATE_UNKNOWN_EXCEPTION = 900104;
  public static final int API_SPACE_CREATE_UNABLE_SET_RXHTML_EXCEPTION = 980204;
  public static final int API_SPACE_CREATE_FAILED_NOT_ADAMA_DEVELOPER = 998384;

  public static final int API_AUTH_DOMAIN_AUTH_DOMAIN_INVALID_MAPPED = 902399;
  public static final int API_AUTH_DOMAIN_AUTH_NO_KEY_MAPPED = 983294;

  public static final int API_AUTH_FAILED_PARSING_PASSWORD = 930039;

  public static final int API_SET_PASSWORD_UNKNOWN_EXCEPTION = 991368;
  public static final int API_SET_PASSWORD_ONLY_ADAMA_DEV_EXCEPTION = 983199;
  public static final int API_SET_PASSWORD_INVALID = 985216;
  public static final int API_LOGIN_UNKNOWN_EXCEPTION = 904327;
  public static final int API_CONVERT_TOKEN_UNKNOWN_EXCEPTION = 978099;
  public static final int API_CONVERT_TOKEN_VALIDATE_EXCEPTION = 904383;

  public static final int API_GET_PAYMENT_INFO_UNKNOWN = 904399;
  public static final int API_GET_PAYMENT_INFO_ONLY_ADAMA_DEV_EXCEPTION = 903375;

  public static final int API_INIT_SETUP_UNKNOWN_EXCEPTION = 965636;
  public static final int API_INIT_FAILED_FIND_UNKNOWN_EXCEPTION = 901363;
  public static final int API_INIT_COMPLETE_UNKNOWN_EXCEPTION = 946179;
  public static final int API_INIT_COMPLETE_CODE_MISMATCH = 916486;

  public static final int API_SPACE_SET_PLAN_UNKNOWN_EXCEPTION = 904318;
  public static final int API_SPACE_SET_PLAN_DEPLOYMENT_FAILED_FINDING_CAPACITY = 965747;
  public static final int API_SPACE_KICK_UNKNOWN_EXCEPTION = 998593;
  public static final int API_SPACE_GET_PLAN_UNKNOWN_EXCEPTION = 913408;
  public static final int API_SPACE_SET_ROLE_UNKNOWN_EXCEPTION = 986120;
  public static final int API_SPACE_LIST_DEVELOPERS_UNKNOWN_EXCEPTION = 988380;
  public static final int API_SPACE_LIST_UNKNOWN_EXCEPTION = 941064;
  public static final int API_SPACE_LIST_NO_PERMISSION_TO_EXECUTE = 920576;
  public static final int API_CREATE_AUTHORITY_UNKNOWN_EXCEPTION = 982016;
  public static final int API_CREATE_AUTHORITY_NO_PERMISSION_TO_EXECUTE = 990208;
  public static final int API_SET_AUTHORITY_UNKNOWN_EXCEPTION = 900098;
  public static final int API_SET_AUTHORITY_NO_PERMISSION_TO_EXECUTE = 970780;
  public static final int API_GET_AUTHORITY_UNKNOWN_EXCEPTION = 928819;
  public static final int API_GET_AUTHORITY_NO_PERMISSION_TO_EXECUTE = 978992;
  public static final int API_LIST_AUTHORITY_UNKNOWN_EXCEPTION = 998430;
  public static final int API_LIST_AUTHORITY_NO_PERMISSION_TO_EXECUTE = 904223;

  public static final int API_DELETE_AUTHORITY_UNKNOWN_EXCEPTION = 913436;
  public static final int API_DELETE_AUTHORITY_NO_PERMISSION_TO_EXECUTE = 901144;

  public static final int API_VALIDATE_DOMAIN_NO_SPACE_FOUND = 990417;
  public static final int API_VALIDATE_DOMAIN_NO_KEY_FOUND = 916689;

  public static final int API_CONNECT_BILING_MUST_BE_ADAMA_DEVELOPER = 903415;

  public static final int API_CREATE_DOCUMENT_SPACE_RESERVED = 995505;
  public static final int API_CREATE_SPACE_RESERVED = 947404;

  public static final int API_DOMAIN_GET_NOT_FOUND = 984080;
  public static final int API_DOMAIN_GET_NOT_AUTHORIZED = 916520;
  public static final int API_DOMAIN_GET_UNKNOWN_EXCEPTION = 997516;

  public static final int API_DOMAIN_MAP_FAILED = 904343;
  public static final int API_DOMAIN_MAP_UNKNOWN_EXCEPTION = 984111;
  public static final int API_DOMAIN_CONFIGURE_UNKNOWN_EXCEPTION = 901421;
  public static final int API_DOMAIN_CONFIGURE_NOT_AUTHORIZED = 982291;

  public static final int API_DOMAIN_LIST_NOT_AUTHORIZED = 928991;
  public static final int API_DOMAIN_LIST_UNKNOWN_EXCEPTION = 901251;
  public static final int API_DOMAIN_LIST_BY_SPACE_UNKNOWN_EXCEPTION = 914676;

  public static final int API_REFLECT_BY_DOMAIN_NOT_AUTHORIZED = 991435;
  public static final int API_DOMAIN_UNMAP_FAILED = 901163;
  public static final int API_DOMAIN_UNMAP_NOT_AUTHORIZED = 916527;
  public static final int API_DOMAIN_UNMAP_UNKNOWN_EXCEPTION = 913447;

  public static final int API_SPACE_SET_RXHTML_UNKNOWN_EXCEPTION = 949427;

  public static final int API_SPACE_SET_POLICY_UNKNOWN_EXCEPTION = 984268;
  public static final int API_SPACE_GET_RXHTML_UNKNOWN_EXCEPTION = 992319;
  public static final int API_SPACE_GET_METRICS_UNKNOWN_EXCEPTION = 999675;

  public static final int API_METHOD_NOT_FOUND = 945213;
  public static final int API_INVALID_KEY_EMPTY = 919676;
  public static final int API_INVALID_KEY_NOT_SIMPLE = 946192;

  public static final int API_INVALID_SPACE_EMPTY = 937076;
  public static final int API_INVALID_SPACE_INAPPROPRIATE_NAME = 904364;
  public static final int API_INVALID_SPACE_NOT_SIMPLE = 998515;
  public static final int API_INVALID_SPACE_HAS_INVALID_CHARACTER = 950465;

  public static final int API_KEYSTORE_NOT_JSON = 998459;
  public static final int API_KEYSTORE_ROOT_ITEM_NOT_OBJECT = 949307;
  public static final int API_KEYSTORE_KEY_LACKS_BYTES64 = 901179;
  public static final int API_KEYSTORE_KEY_LACKS_VALID_BYTES64 = 987191;

  public static final int API_KEYSTORE_KEY_LACKS_ALGO = 967735;
  public static final int API_KEYSTORE_KEY_LACKS_VALID_ALGO = 907319;
  public static final int API_KEYSTORE_KEY_INTERNAL_ERROR = 952372;

  public static final int API_ASSET_FAILED_BIND = 919601;

  public static final int API_CLOUD_RESTORE_FAILED = 904348;

  @Description("Corruption between client and server caused a chunk fail an integrity check")
  public static final int API_ASSET_CHUNK_BAD_DIGEST = 999472;

  public static final int API_ASSET_CHUNK_UNKNOWN_EXCEPTION = 994352;

  @User
  @Description("The asset attachemnt will fail due to not being allowed")
  public static final int API_ASSET_ATTACHMENT_NOT_ALLOWED = 966768;

  @Description("The asset attachment failed due to losing the connection to the document")
  public static final int API_ASSET_ATTACHMENT_LOST_CONNECTION = 920719;
  public static final int API_ASSET_ATTACHMENT_UNKNOWN_EXCEPTION = 985219;

  public static final int API_LIST_DOCUMENTS_UNKNOWN_EXCEPTION = 903232;
  public static final int API_SPACE_DELETE_UNKNOWN_EXCEPTION = 904256;
  public static final int API_SPACE_DELETE_NOT_EMPTY = 998495;

  public static final int API_GENERATE_KEY_UNKNOWN_EXCEPTION = 925840;

  public static final int API_CHANNEL_VALIDATION_FAILED_EMPTY = 950399;
  public static final int API_CHANNEL_VALIDATION_BAD_START_CHARACTER = 908415;
  public static final int API_CHANNEL_VALIDATION_BAD_MIDDLE_CHARACTER = 967804;

  @Description("The given document ran out of compute tokens. This is most likely due to an infinite loop.")
  public static final int API_GOODWILL_EXCEPTION = 950384;

  public static final int AWS_EMAIL_SEND_FAILURE_HARD_EXCEPTION = 982212;
  public static final int AWS_EMAIL_SEND_FAILURE_EXCEPTION = 901232;
  public static final int AWS_EMAIL_SEND_FAILURE_NOT_200 = 986308;

  public static final int WEB_BASE_FILE_WRITER_IOEXCEPTION_CREATE = 928944;
  public static final int WEB_BASE_FILE_WRITER_NOT_200 = 903347;
  public static final int WEB_BASE_FILE_WRITER_PREMATURE_END = 986319;
  public static final int WEB_BASE_FILE_WRITER_IOEXCEPTION_CLOSE = 993487;
  public static final int WEB_BASE_FILE_WRITER_IOEXCEPTION_FRAGMENT = 913615;

  public static final int WEB_BASE_EXECUTE_FAILED_CONNECT = 921794;
  public static final int WEB_BASE_EXECUTE_FAILED_EXCEPTION_CAUGHT = 950469;
  public static final int WEB_BASE_SHARED_EXECUTE_FAILED_READ = 934083;
  public static final int WEB_BASE_EXECUTE_SHARED_TOO_MANY_INFLIGHT = 977091;

  public static final int NET_LCSM_PASSWORD_TIMEOUT = 990447;
  public static final int NET_LCSM_PASSWORD_REJECTED = 915695;
  public static final int NET_LCSM_UPDATE_TIMEOUT = 998434;
  public static final int NET_LCSM_UPDATE_REJECTED = 930848;
  public static final int NET_LCSM_SEND_TIMEOUT = 901180;
  public static final int NET_LCSM_SEND_REJECTED = 912444;
  public static final int NET_LCSM_CAN_ATTACH_TIMEOUT = 912447;
  public static final int NET_LCSM_CAN_ATTACH_REJECTED = 962655;
  public static final int NET_LCSM_ATTACH_TIMEOUT = 996436;
  public static final int NET_LCSM_ATTACH_REJECTED = 969806;

  @RetryInternally
  public static final int NET_RTT_MACHINE_NOT_FOUND = 902223;

  @RetryInternally
  public static final int NET_LCSM_NO_MACHINE_FOUND = 912544;

  @RetryInternally
  public static final int NET_LCSM_DISCONNECTED_PREMATURE = 983117;

  @RetryInternally
  public static final int NET_LCSM_TIMEOUT = 928828;

  public static final int WEB_CALLBACK_RESOURCE_GONE = 984312;
  public static final int WEB_CALLBACK_RESOURCE_NOT_FOUND = 986396;
  public static final int WEB_CALLBACK_RESOURCE_NOT_AUTHORIZED = 982272;

  public static final int WEB_VOID_CALLBACK_NOT_200 = 931015;
  public static final int WEB_STRING_CALLBACK_NOT_200 = 979143;
  public static final int WEB_BYTEARRAY_CALLBACK_NOT_200 = 924928;

  public static final int UPLOAD_SCAN_FILE_FAILURE = 903364;
  public static final int BACKUP_FILE_FAILURE = 998599;
  public static final int STREAM_ASSET_NOT_200 = 967879;
  public static final int LIST_ASSETS_PARSE_FAILURE = 929990;
  public static final int STREAM_ASSET_CORRUPTED = 994558;

  public static final int SUPER_NOT_AUTHORIZED_LIST = 920777;
  public static final int SUPER_NOT_AUTHORIZED_CHECKIN = 919774;
  public static final int SUPER_UNEXPECTED_EXCEPTION_CHECKIN = 914626;
  public static final int SUPER_NOT_AUTHORIZED_SET_AUTOMATIC = 979145;
  public static final int SUPER_INVALID_CERT_SET_AUTOMATIC = 921823;

  public static final int SUPER_UNEXPECTED_EXCEPTION_LIST = 904394;
  public static final int SUPER_UNEXPECTED_EXCEPTION_SET_AUTOMATIC = 999619;

  public static final int FRONTEND_NO_HOST_HEADER = 979194;
  public static final int FRONTEND_NO_DOMAIN_MAPPING = 973020;
  public static final int FRONTEND_DELETE_INVALID = 922943;
  public static final int FRONTEND_IP_DONT_RESOLVE = 909532;
  public static final int FRONTEND_SECRETS_SIGNING_EXCEPTION = 917740;

  public static final int CACHE_ASSET_FILE_FAILED_WRITE = 929987;
  public static final int CACHE_ASSET_FILE_CLOSED_PRIOR_ATTACH = 920811;
  public static final int CACHE_ASSET_FILE_FAILED_CREATE = 995563;


  public static final int GLOBAL_CAPACITY_EXCEPTION_ADD = 973839;
  public static final int GLOBAL_CAPACITY_EXCEPTION_REMOVE = 950322;
  public static final int GLOBAL_CAPACITY_EXCEPTION_NUKE = 966671;

  public static final int GLOBAL_CAPACITY_EXCEPTION_LIST_SPACE = 937164;
  public static final int GLOBAL_CAPACITY_EXCEPTION_LIST_MACHINE = 909563;
  public static final int GLOBAL_CAPACITY_EXCEPTION_LIST_REGION = 997631;
  public static final int GLOBAL_CAPACITY_EXCEPTION_PICKHOST = 993504;
  public static final int GLOBAL_CAPACITY_EMPTY_PICKHOST = 998625;
  public static final int GLOBAL_CAPACITY_EXCEPTION_NEWHOST = 998624;
  public static final int GLOBAL_CAPACITY_EMPTY_NEWHOST = 901348;

  public static final int GLOBAL_FAILED_HOST_INIT = 904461;

  public static final int GLOBAL_DOMAIN_FIND_EXCEPTION = 966888;

  @RetryInternally
  public static final int FAILED_FIND_LOCAL_CAPACITY = 978162;

  public static final int FAILED_DEINIT_SPACES_EXIST = 999665;
  public static final int FAILED_DEINIT_AUTHORITIES_EXIST = 918771;
  public static final int FAILED_DEINIT_DOMAINS_EXIST = 970995;
  public static final int FAILED_DEINIT_UNKONWN_EXCEPTION = 989427;

  public static final int AUTH_COOKIE_CANT_STASH_COOKIE = 909436;
  public static final int AUTH_IDENTITIES_NOT_AVAILABLE = 930111;
  /**
   * 997647 971023 934159 903435 969987 946435
   * 901428 955700 991540 904502 998708 995636 986419 966962 916784 903475 904499 930097 995663
   * 933196 998732 995695 970108 916924 998839 932279 995763 921008 996784 920015 995788 904648
   * 981451 968135 990658 946627 904643 916930 983506 903660 967148 998892 986594 983551 913916
   * 970239 901628 933372 902655 918011 933368 998904 913908 913904 975344 991757 933388 903695
   * 918027 995844 998915 967170 917020 983587 984609 909884 980543 913983 971324 998967 983604
   * 901680 967216 900659 979535 909903 901743 914046 990863 982671 915118 996031 931516 914111
   * 975548 984755 904880 904909 975567 983757 925388 904897 910016 983746 901827 950976 917186
   * 914140 901852 967379 913107 909039 921324 999139 934654 910077 970495 915196 999155 984818
   * 901872 990960 904946 987888 995087 922383 999182 982799 905996 978703 900876 997133 981772
   * 900879 918285 996107 931595 901899 979721 917258 914181 926471 917255 934658 917249 954115
   * 959235 981763 904963 996096 992000 998144 989952 921374 989983 967455 991006 986905 982808
   * 982804 930578 979730 904977 960272 933648 903955 992016 986896 917266 921388 996141 992044
   * 914212 985891 901922 967486 947007 975676 910143 974653 971574 917300 982839 966452 931637
   * 978738 906033 980787 985906 986930 925488 916275 915250 997199 919372 916303 997196 905032
   * 909120 916288 964419 917340 905055 918360 929616 977745 999272 999271 921442 933731 906108
   * 979839 918396 931708 909183 903038 928635 987000 905077 979829 904051 985968 930673 995184
   * 997263 914319 982924 906123 914308 999301 987012 983940 983938 995200 983952 979885 997311
   * 906172 917439 909247 914360 991159 901041 904112 909235 926640 930736 985039 921550 930766
   * 942031 992206 921551 904143 988109 950220 954317 997320 992200 918470 984007 921540 986052
   * 933826 966595 985026 969667 930755 979907 917443 933824 996288 905180 967644 918492 987092
   * 984019 954323 930771 999378 950224 930768 984047 918508 917486 984044 914403 999423 917501
   * 906236 937980 916479 995324 967677 918525 984059 987131 967675 917496 969720 967672 988152
   * 930807 994291 901104 967667 991218 998385
   */
  public static final int SERVICE_CONFIG_BAD_INTEGER_FROM_STRING = 798735;
  public static final int SERVICE_CONFIG_BAD_INTEGER = 777231;
  public static final int SERVICE_CONFIG_BAD_STRING_TYPE = 701452;
  public static final int SERVICE_CONFIG_BAD_STRING_NOT_PRESENT = 786442;

  public static final int SERVICE_CONFIG_BAD_ENCRYPT_STRING_NOT_PRESENT_OR_WRONG_TYPE = 723982;
  public static final int SERVICE_CONFIG_BAD_ENCRYPT_STRING_NO_KEYID_ETC = 786441;
  public static final int SERVICE_CONFIG_BAD_ENCRYPT_STRING_KEYID_INVALID = 716812;
  public static final int SERVICE_CONFIG_BAD_PRIVATE_KEY_BUNDLE = 785601;
  public static final int SERVICE_CONFIG_BAD_ENCRYPT_STRING_FAILED_SECRET_KEY_LOOKUP = 791567;
  public static final int SERVICE_CONFIG_BAD_ENCRYPT_STRING_FAILED_DECRYPTION = 782348;
  public static final int MYSQL_FAILED_FINDING_SECRET_KEY = 786436;
  public static final int MYSQL_FAILED_FINDING_SENTINEL_ASPECT = 790704;
  public static final int MYSQL_FAILED_FINDING_SENTINEL_COUNT = 775372;

  public static final int NET_HANDLER_SCAN_EXCEPTION = 716806;

  public static final int NET_DISCONNECT = 773155;
  public static final int NET_SHUTTING_DOWN = 788515;
  public static final int NET_CONNECT_FAILED_UNKNOWN = 798756;
  public static final int NET_CONNECT_FAILED_TO_CONNECT = 724001;
  public static final int NET_FINDER_GAVE_UP = 705583;
  public static final int NET_FINDER_FAILED_PICK_HOST = 776396;
  public static final int NET_FINDER_ROUTER_REGION_NOT_EXPECTED = 724012;
  public static final int NET_FINDER_ROUTER_NULL_MACHINE = 783395;

  public static final int ADAMA_NET_PING_TIMEOUT = 773152;
  public static final int ADAMA_NET_PING_REJECTED = 786466;
  public static final int ADAMA_NET_REFLECT_TIMEOUT = 799728;
  public static final int ADAMA_NET_REFLECT_REJECTED = 787440;
  public static final int ADAMA_NET_CREATE_TIMEOUT = 720955;
  public static final int ADAMA_NET_CREATE_FOUND_REGION_RATHER_THAN_MACHINE = 717952;
  public static final int ADAMA_NET_DELETE_FOUND_REGION_RATHER_THAN_MACHINE = 789668;
  public static final int ADAMA_NET_DIRECTSEND_FOUND_REGION_RATHER_THAN_MACHINE = 750799;
  public static final int ADAMA_NET_REFLECT_FOUND_REGION_RATHER_THAN_MACHINE = 752841;
  public static final int ADAMA_NET_AUTH_FOUND_REGION_RATHER_THAN_MACHINE = 797872;
  public static final int ADAMA_NET_WEBGET_FOUND_REGION_RATHER_THAN_MACHINE = 797824;
  public static final int ADAMA_NET_WEBOPTIONS_FOUND_REGION_RATHER_THAN_MACHINE = 773309;
  public static final int ADAMA_NET_WEBPUT_FOUND_REGION_RATHER_THAN_MACHINE = 703539;
  public static final int ADAMA_NET_WEBDELETE_FOUND_REGION_RATHER_THAN_MACHINE = 787645;
  public static final int ADAMA_NET_CREATE_REJECTED = 737336;

  public static final int ADAMA_NET_DIRECTSEND_TIMEOUT = 787656;
  public static final int ADAMA_NET_DIRECTSEND_REJECTED = 729294;

  public static final int ADAMA_NET_DELETE_TIMEOUT = 716947;
  public static final int ADAMA_NET_DELETE_REJECTED = 798890;

  public static final int ADAMA_NET_CLOSE_TIMEOUT = 788543;
  public static final int ADAMA_NET_CLOSE_REJECTED = 796735;
  public static final int ADAMA_NET_METERING_TIMEOUT = 770104;
  public static final int ADAMA_NET_METERING_REJECTED = 786495;
  public static final int ADAMA_NET_SCAN_DEPLOYMENT_TIMEOUT = 715839;
  public static final int ADAMA_NET_SCAN_DEPLOYMENT_REJECTED = 787514;
  public static final int ADAMA_NET_RATE_LIMIT_TIMEOUT = 733378;
  public static final int ADAMA_NET_RATE_LIMIT_REJECTED = 798918;

  @RetryInternally
  public static final int ADAMA_NET_CONNECT_DOCUMENT_TIMEOUT = 718908;
  public static final int ADAMA_NET_CONNECT_DOCUMENT_REJECTED = 702524;
  public static final int ADAMA_NET_WEBGET_TIMEOUT = 783411;
  public static final int ADAMA_NET_WEBGET_REJECTED = 724019;
  public static final int ADAMA_NET_WEBPUT_TIMEOUT = 792631;
  public static final int ADAMA_NET_WEBPUT_REJECTED = 732208;
  public static final int ADAMA_NET_WEBOPTIONS_TIMEOUT = 703667;
  public static final int ADAMA_NET_WEBOPTIONS_REJECTED = 786615;
  public static final int ADAMA_NET_AUTH_TIMEOUT = 793779;
  public static final int ADAMA_NET_AUTH_REJECTED = 790732;
  public static final int ADAMA_NET_DRAIN_TIMEOUT = 722131;
  public static final int ADAMA_NET_DRAIN_REJECTED = 787692;
  public static final int ADAMA_NET_GETLOAD_TIMEOUT = 787664;
  public static final int ADAMA_NET_GETLOAD_REJECTED = 774354;


  public static final int ADAMA_NET_PROBE_TIMEOUT = 756928;
  public static final int ADAMA_NET_PROBE_REJECTED = 740544;

  public static final int ADAMA_NET_FIND_TIMEOUT = 725184;
  public static final int ADAMA_NET_FIND_REJECTED = 753699;

  @RetryInternally
  public static final int ADAMA_NET_CONNECTION_DONE = 769085;
  public static final int ADAMA_NET_INVALID_TARGET = 719932;
  public static final int ADAMA_NET_FAILED_FIND_TARGET = 753724;
  public static final int ADAMA_NET_FAILED_FINDING_SUBID = 797755;

  public static final int CARAVAN_COMPUTE_METHOD_NOT_FOUND = 785491;
  public static final int CARAVAN_COMPUTE_REWIND_SEQ_NOT_FOUND = 791602;

  @RetryInternally
  public static final int CARAVAN_COMPUTE_HEADPATCH_SEQ_NOT_FOUND = 787507;

  public static final int CARAVAN_UNABLE_TO_COMPACT_NON_POSITIVE_HISTORY = 734263;
  public static final int CARAVAN_OUT_OF_SPACE_SNAPSHOT = 785490;
  public static final int CARAVAN_OUT_OF_SPACE_PATCH = 788560;
  public static final int CARAVAN_OUT_OF_SPACE_INITIALIZE = 773242;
  public static final int CARAVAN_LOAD_FAILURE_EXCEPTION = 789645;

  public static final int CARAVAN_CANT_BACKUP_EXCEPTION = 793742;
  public static final int CARAVAN_CANT_RESTORE_EXCEPTION = 739471;
  public static final int CARAVAN_CANT_RESTORE_CANT_READ = 733327;
  public static final int CARAVAN_CANT_MERGE_RESTORE_OUT_OF_SPACE = 720012;

  public static final int FINDER_SERVICE_MYSQL_FREE_EXCEPTION = 771139;
  public static final int FINDER_SERVICE_MYSQL_CANT_MARK_DELETE = 773247;
  public static final int FINDER_SERVICE_MYSQL_CANT_COMMIT_DELETE = 787655;
  public static final int FINDER_SERVICE_MYSQL_FIND_EXCEPTION = 785528;
  public static final int FINDER_SERVICE_MYSQL_LIST_EXCEPTION = 735364;
  public static final int FINDER_SERVICE_MYSQL_MARK_DELETE_EXCEPTION = 777331;
  public static final int FINDER_SERVICE_MYSQL_COMMIT_DELETE_EXCEPTION = 784576;
  public static final int FINDER_SERVICE_MYSQL_CANT_BACKUP = 722034;
  public static final int FINDER_SERVICE_MYSQL_BACKUP_EXCEPTION = 716912;

  public static final int PROXY_TIMEOUT = 736347;
  public static final int PROXY_REJECTED = 799836;

  public static final int MANAGED_STORAGE_WRONG_MACHINE = 735344;
  public static final int MANAGED_STORAGE_NULL_ARCHIVE = 778433;
  public static final int MANAGED_STORAGE_WRITE_FAILED_CLOSED = 734320;
  public static final int MANAGED_STORAGE_READ_FAILED_CLOSED = 768112;
  public static final int MANAGED_STORAGE_CLOSED_BEFORE_FOUND = 791691;
  public static final int MANAGED_STORAGE_DELETED = 786620;

  public static final int WEBBASE_LOST_CONNECTION = 787632;
  public static final int WEBBASE_CONNECTION_CLOSE = 770224;
  public static final int WEBBASE_CONNECT_TIMEOUT = 772272;
  public static final int WEBBASE_CONNECT_REJECTED = 721095;

  public static final int MULTI_REGION_CLIENT_NO_ROUTE = 716993;

  /**
   * 781507 752835
   * 711876 704708 785604 723140 790748 736472 781535 789720 729311
   * 788717 707816 797934 793839 735471 752879 766191 787684 798944 793824 797923 736484 752891
   * 782585 749821 782589 707836 778492 758012 736508 798971 777459 784627 719091 705779 772337
   * 751857 756976 720113 746736 725239 770295 796912 708855 777460 797939 798963 736500 785673
   * 769295 722188 777491 797971 717103 732460 799012 790819 793916 799037 788798 721208 789823
   * 737585 724279 782671 707916 794972 799086 773484 736635 785791 782719 789879 798064 789874
   * 705920 799104 789891 770463 789920 786866 795085 786893 773576 782798 799178 774604 799172
   * 794052 708032 774592 773574 733672 782831 707040 725472 786940 799228 795134 786936 782847
   * 740851 736752 791024 702964 796147 791027 795150 785933 770572 750092 769548 754188 736780
   * 757251 733696 797184 770567 721412 790019 769552 773648 799249 790035 708128 793120 790049
   * 799267 770619 717374 774716 781875 719411 724528 788016 790064 789041 797235 705100 717376
   * 744038 774775 795251 786050 772771 706214 746150 799423 798414 721615 701135 720588 790219
   * 701120 705217 736964 773852 790224 790252 798444 705263 795391 766719 784126 733948 790260
   * 708336 789238 788237 720648 789262 797455 790287 733967 736015 777999 726799 751374 771853
   * 708365 724748 788228 729859 799492 716547 790277 717571 762625 705281 728832 797440 737031
   * 790273 796419 791299 787231 733983 773919 770835 783123 782097 786192 720661 737067 797484
   * 736040 798504 788264 757550 799530 774957 783139 704288 771899 795455 769848 778040 779071
   * 768831 782143 723775 798520 756543 725823 786239 724799 718652 774963 737075 711475 762673
   * 718640 728880 750384 705329 796464 774967 773943 770869 787250 770895 774991 779084 739148
   * 790342 741184 783187 793425 771948 705400 786296 788351 794488 721791 786303 724860 746364
   * 789360 796531 787315 799628 789390 783247 798600 773007 712591 788361 774028 725900 790403
   * 787331 799640 782239 770991 705452 724908 787361 790462 789433 784316 757683 753587 721841
   * 786359 787404 797644 732107 791501 782280 753608 774088 741320 792527 753615 745423 785358
   * 784334 786382 795594 721868 735180 781260 779212 720835 787397 791493 790471 721856 786375
   * 789441 709575 794562 792515 749508 720859 787423 779231 705503 798683 753628 737244 712659
   * 783313 734160 795601 799725 788463 736239 779247 742383 784367 723951 705517 770028 705507
   * 720867 717792 720871 799714 726011 796668 717816 740351 705535 785405 717820 712700 753651
   *
   */

  // TODO: nuke prior WAL implementation
  public static final int CARAVAN_KEY_NOT_LOADED_PATCH = 707675;
  public static final int CARAVAN_KEY_NOT_LOADED_COMPUTE = 790622;
  public static final int CARAVAN_KEY_NOT_LOADED_SNAPSHOT = 790623;

  public static final int GRPC_HANDLER_EXCEPTION = 734211;
  public static final int GRPC_STREAM_ASK_TIMEOUT = 774147;
  public static final int GRPC_STREAM_ASK_REJECTED = 782339;

  public static final int GRPC_STREAM_ATTACH_TIMEOUT = 751618;
  public static final int GRPC_STREAM_ATTACH_REJECTED = 733185;

  public static final int GRPC_STREAM_SEND_TIMEOUT = 768000;
  public static final int GRPC_STREAM_SEND_REJECTED = 754688;

  public static final int GRPC_STREAM_UPDATE_TIMEOUT = 786433;
  public static final int GRPC_STREAM_UPDATE_REJECTED = 796674;

  public static final int DISK_GET_IO_EXCEPTION = 716804;
  public static final int DISK_INITIALIZE_IO_EXCEPTION = 793602;
  public static final int DISK_UNABLE_TO_PATCH_FILE_NOT_FOUND = 794627;
  public static final int DISK_PATCH_IO_EXCEPTION = 777244;
  public static final int DISK_UNABLE_TO_DELETE = 769042;
  public static final int DISK_UNABLE_TO_COMPACT_FILE_NOT_FOUND = 784401;
  public static final int DISK_UNABLE_TO_COMPACT_NON_POSITIVE_HISTORY = 777259;
  public static final int DISK_COMPACT_READ_IO_EXCEPTION = 736272;
  public static final int DISK_COMPACT_WRITE_IO_EXCEPTION = 739351;
  public static final int DISK_UNABLE_TO_COMPUTE_FILE_NOT_FOUND = 790544;

  public static final int DISK_COMPUTE_HEADPATCH_NOTHING_TO_DO = 725039;

  public static final int VAPID_NOT_FOUND_FOR_DOMAIN = 792643;
  public static final int CONFIG_NOT_FOUND_FOR_DOMAIN = 736243;

  public static final int FIRST_PARTY_SERVICES_METHOD_NOT_FOUND = 888888;
  public static final int FIRST_PARTY_SERVICES_METHOD_NOT_IMPLEMENTED = 888889;
  public static final int FIRST_PARTY_SERVICES_METHOD_EXCEPTION = 888890;

  public static final int FIRST_PARTY_AMAZON_SES_FAILURE = 823242;
  public static final int FIRST_PARTY_AMAZON_SES_MISSING_ARGS = 823243;

  public static final int FIRST_PARTY_GOOGLE_UNKNOWN_FAILURE = 758768;
  public static final int FIRST_PARTY_GOOGLE_MISSING_EMAIL = 709617;

  public static final int GLOBAL_PUSHER_UNKNOWN_FAILURE = 789495;
  public static final int PUSH_REGISTER_UNKNOWN_FAILURE = 737266;
  public static final int VAPID_CREATE_UNKNOWN_FAILURE = 774130;

  public static final int JITSI_FAILED_SIGNING_TOKEN = 783347;


  public static final int CUSTOMER_BACKUP_DOWNLOAD_NO_ARCHIVE_YET = 707651;
  public static final int CUSTOMER_BACKUP_DOWNLOAD_FAILED = 788607;

  public static final int DEVBOX_NO_SECRET_FOR_KEY = 728051;

}
