/*
 * This file is subject to the terms and conditions outlined in the file 'LICENSE' (hint: it's MIT); this file is located in the root directory near the README.md which you should also read.
 *
 * This file is part of the 'Adama' project which is a programming language and document store for board games; however, it can be so much more.
 *
 * See http://www.adama-lang.org/ for more information.
 *
 * (c) 2020 - 2022 by Jeffrey M. Barber (http://jeffrey.io)
 */
package org.adamalang.web.service;

import java.nio.charset.StandardCharsets;

import java.util.Base64;

public class JavaScriptClient {
  public static final byte[] ADAMA_JS_CLIENT_BYTES = Base64.getDecoder().decode("IWZ1bmN0aW9uKGUsdCl7Im9iamVjdCI9PXR5cGVvZiBleHBvcnRzJiYib2JqZWN0Ij09dHlwZW9mIG1vZHVsZT9tb2R1bGUuZXhwb3J0cz10KCk6ImZ1bmN0aW9uIj09dHlwZW9mIGRlZmluZSYmZGVmaW5lLmFtZD9kZWZpbmUoW10sdCk6Im9iamVjdCI9PXR5cGVvZiBleHBvcnRzP2V4cG9ydHMuQWRhbWE9dCgpOmUuQWRhbWE9dCgpfSh0aGlzLChmdW5jdGlvbigpe3JldHVybiBmdW5jdGlvbihlKXt2YXIgdD17fTtmdW5jdGlvbiBuKHIpe2lmKHRbcl0pcmV0dXJuIHRbcl0uZXhwb3J0czt2YXIgaT10W3JdPXtpOnIsbDohMSxleHBvcnRzOnt9fTtyZXR1cm4gZVtyXS5jYWxsKGkuZXhwb3J0cyxpLGkuZXhwb3J0cyxuKSxpLmw9ITAsaS5leHBvcnRzfXJldHVybiBuLm09ZSxuLmM9dCxuLmQ9ZnVuY3Rpb24oZSx0LHIpe24ubyhlLHQpfHxPYmplY3QuZGVmaW5lUHJvcGVydHkoZSx0LHtlbnVtZXJhYmxlOiEwLGdldDpyfSl9LG4ucj1mdW5jdGlvbihlKXsidW5kZWZpbmVkIiE9dHlwZW9mIFN5bWJvbCYmU3ltYm9sLnRvU3RyaW5nVGFnJiZPYmplY3QuZGVmaW5lUHJvcGVydHkoZSxTeW1ib2wudG9TdHJpbmdUYWcse3ZhbHVlOiJNb2R1bGUifSksT2JqZWN0LmRlZmluZVByb3BlcnR5KGUsIl9fZXNNb2R1bGUiLHt2YWx1ZTohMH0pfSxuLnQ9ZnVuY3Rpb24oZSx0KXtpZigxJnQmJihlPW4oZSkpLDgmdClyZXR1cm4gZTtpZig0JnQmJiJvYmplY3QiPT10eXBlb2YgZSYmZSYmZS5fX2VzTW9kdWxlKXJldHVybiBlO3ZhciByPU9iamVjdC5jcmVhdGUobnVsbCk7aWYobi5yKHIpLE9iamVjdC5kZWZpbmVQcm9wZXJ0eShyLCJkZWZhdWx0Iix7ZW51bWVyYWJsZTohMCx2YWx1ZTplfSksMiZ0JiYic3RyaW5nIiE9dHlwZW9mIGUpZm9yKHZhciBpIGluIGUpbi5kKHIsaSxmdW5jdGlvbih0KXtyZXR1cm4gZVt0XX0uYmluZChudWxsLGkpKTtyZXR1cm4gcn0sbi5uPWZ1bmN0aW9uKGUpe3ZhciB0PWUmJmUuX19lc01vZHVsZT9mdW5jdGlvbigpe3JldHVybiBlLmRlZmF1bHR9OmZ1bmN0aW9uKCl7cmV0dXJuIGV9O3JldHVybiBuLmQodCwiYSIsdCksdH0sbi5vPWZ1bmN0aW9uKGUsdCl7cmV0dXJuIE9iamVjdC5wcm90b3R5cGUuaGFzT3duUHJvcGVydHkuY2FsbChlLHQpfSxuLnA9IiIsbihuLnM9MSl9KFtmdW5jdGlvbihlLHQsbil7KGZ1bmN0aW9uKHQpe3ZhciBuPW51bGw7InVuZGVmaW5lZCIhPXR5cGVvZiBXZWJTb2NrZXQ/bj1XZWJTb2NrZXQ6InVuZGVmaW5lZCIhPXR5cGVvZiBNb3pXZWJTb2NrZXQ/bj1Nb3pXZWJTb2NrZXQ6dm9pZCAwIT09dD9uPXQuV2ViU29ja2V0fHx0Lk1veldlYlNvY2tldDoidW5kZWZpbmVkIiE9dHlwZW9mIHdpbmRvdz9uPXdpbmRvdy5XZWJTb2NrZXR8fHdpbmRvdy5Nb3pXZWJTb2NrZXQ6InVuZGVmaW5lZCIhPXR5cGVvZiBzZWxmJiYobj1zZWxmLldlYlNvY2tldHx8c2VsZi5Nb3pXZWJTb2NrZXQpLGUuZXhwb3J0cz1ufSkuY2FsbCh0aGlzLG4oMikpfSxmdW5jdGlvbihlLHQsbil7InVzZSBzdHJpY3QiO24ucih0KSxuLmQodCwiUHJvZHVjdGlvbiIsKGZ1bmN0aW9uKCl7cmV0dXJuIGN9KSksbi5kKHQsIlRyZWUiLChmdW5jdGlvbigpe3JldHVybiB1fSkpLG4uZCh0LCJDb25zb2xlTG9nU2VxUmVzcG9uZGVyIiwoZnVuY3Rpb24oKXtyZXR1cm4gYX0pKSxuLmQodCwiVHJlZVBpcGVEYXRhUmVzcG9uc2UiLChmdW5jdGlvbigpe3JldHVybiBkfSkpLG4uZCh0LCJDb25uZWN0aW9uIiwoZnVuY3Rpb24oKXtyZXR1cm4gaH0pKTt2YXIgcj1uKDApLGk9bi5uKHIpLG89ZnVuY3Rpb24oZSx0LG4scil7cmV0dXJuIG5ldyhufHwobj1Qcm9taXNlKSkoKGZ1bmN0aW9uKGksbyl7ZnVuY3Rpb24gcyhlKXt0cnl7dShyLm5leHQoZSkpfWNhdGNoKGUpe28oZSl9fWZ1bmN0aW9uIGMoZSl7dHJ5e3Uoci50aHJvdyhlKSl9Y2F0Y2goZSl7byhlKX19ZnVuY3Rpb24gdShlKXt2YXIgdDtlLmRvbmU/aShlLnZhbHVlKToodD1lLnZhbHVlLHQgaW5zdGFuY2VvZiBuP3Q6bmV3IG4oKGZ1bmN0aW9uKGUpe2UodCl9KSkpLnRoZW4ocyxjKX11KChyPXIuYXBwbHkoZSx0fHxbXSkpLm5leHQoKSl9KSl9LHM9ZnVuY3Rpb24oZSx0KXt2YXIgbixyLGksbyxzPXtsYWJlbDowLHNlbnQ6ZnVuY3Rpb24oKXtpZigxJmlbMF0pdGhyb3cgaVsxXTtyZXR1cm4gaVsxXX0sdHJ5czpbXSxvcHM6W119O3JldHVybiBvPXtuZXh0OmMoMCksdGhyb3c6YygxKSxyZXR1cm46YygyKX0sImZ1bmN0aW9uIj09dHlwZW9mIFN5bWJvbCYmKG9bU3ltYm9sLml0ZXJhdG9yXT1mdW5jdGlvbigpe3JldHVybiB0aGlzfSksbztmdW5jdGlvbiBjKG8pe3JldHVybiBmdW5jdGlvbihjKXtyZXR1cm4gZnVuY3Rpb24obyl7aWYobil0aHJvdyBuZXcgVHlwZUVycm9yKCJHZW5lcmF0b3IgaXMgYWxyZWFkeSBleGVjdXRpbmcuIik7Zm9yKDtzOyl0cnl7aWYobj0xLHImJihpPTImb1swXT9yLnJldHVybjpvWzBdP3IudGhyb3d8fCgoaT1yLnJldHVybikmJmkuY2FsbChyKSwwKTpyLm5leHQpJiYhKGk9aS5jYWxsKHIsb1sxXSkpLmRvbmUpcmV0dXJuIGk7c3dpdGNoKHI9MCxpJiYobz1bMiZvWzBdLGkudmFsdWVdKSxvWzBdKXtjYXNlIDA6Y2FzZSAxOmk9bzticmVhaztjYXNlIDQ6cmV0dXJuIHMubGFiZWwrKyx7dmFsdWU6b1sxXSxkb25lOiExfTtjYXNlIDU6cy5sYWJlbCsrLHI9b1sxXSxvPVswXTtjb250aW51ZTtjYXNlIDc6bz1zLm9wcy5wb3AoKSxzLnRyeXMucG9wKCk7Y29udGludWU7ZGVmYXVsdDppZighKGk9cy50cnlzLChpPWkubGVuZ3RoPjAmJmlbaS5sZW5ndGgtMV0pfHw2IT09b1swXSYmMiE9PW9bMF0pKXtzPTA7Y29udGludWV9aWYoMz09PW9bMF0mJighaXx8b1sxXT5pWzBdJiZvWzFdPGlbM10pKXtzLmxhYmVsPW9bMV07YnJlYWt9aWYoNj09PW9bMF0mJnMubGFiZWw8aVsxXSl7cy5sYWJlbD1pWzFdLGk9bzticmVha31pZihpJiZzLmxhYmVsPGlbMl0pe3MubGFiZWw9aVsyXSxzLm9wcy5wdXNoKG8pO2JyZWFrfWlbMl0mJnMub3BzLnBvcCgpLHMudHJ5cy5wb3AoKTtjb250aW51ZX1vPXQuY2FsbChlLHMpfWNhdGNoKGUpe289WzYsZV0scj0wfWZpbmFsbHl7bj1pPTB9aWYoNSZvWzBdKXRocm93IG9bMV07cmV0dXJue3ZhbHVlOm9bMF0/b1sxXTp2b2lkIDAsZG9uZTohMH19KFtvLGNdKX19fSxjPSJhd3MtdXMtZWFzdC0yLmFkYW1hLXBsYXRmb3JtLmNvbSIsdT1mdW5jdGlvbigpe2Z1bmN0aW9uIGUoKXt0aGlzLnRyZWU9e30sdGhpcy5kaXNwYXRjaD17fSx0aGlzLmRpc3BhdGNoX2NvdW50PTAsdGhpcy5xdWV1ZT1bXSx0aGlzLm9uZGVjaWRlPWZ1bmN0aW9uKGUpe319cmV0dXJuIGUucHJvdG90eXBlLl9fcmVjQXBwZW5kQ2hhbmdlPWZ1bmN0aW9uKGUsdCxuLHIpe2lmKEFycmF5LmlzQXJyYXkodCkpZm9yKHZhciBpPTA7aTx0Lmxlbmd0aDtpKyspdGhpcy5fX3JlY0FwcGVuZENoYW5nZShlLHRbaV0sbixyKTtlbHNlIGlmKCJvYmplY3QiPT10eXBlb2YgdClmb3IodmFyIG8gaW4gdClvIGluIGV8fChlW29dPXt9KSx0aGlzLl9fcmVjQXBwZW5kQ2hhbmdlKGVbb10sdFtvXSxuLHIpO2Vsc2UiZnVuY3Rpb24iPT10eXBlb2YgdCYmKCJAZSJpbiBlfHwoZVsiQGUiXT1bXSksZVsiQGUiXS5wdXNoKHtjYjp0LG9yZGVyOm4sYXV0b19kZWxldGU6cn0pKX0sZS5wcm90b3R5cGUuX19hdXRvRGVsZXRlPWZ1bmN0aW9uKGUpe2lmKCJAZSJpbiBlKXtmb3IodmFyIHQ9ZVsiQGUiXSxuPVtdLHI9MDtyPHQubGVuZ3RoO3IrKyl0W3JdLmF1dG9fZGVsZXRlfHxuLnB1c2godFtyXSk7ZVsiQGUiXT1ufX0sZS5wcm90b3R5cGUub25UcmVlQ2hhbmdlPWZ1bmN0aW9uKGUpe3RoaXMuX19yZWNBcHBlbmRDaGFuZ2UodGhpcy5kaXNwYXRjaCxlLHRoaXMuZGlzcGF0Y2hfY291bnQsITEpLHRoaXMuZGlzcGF0Y2hfY291bnQrK30sZS5wcm90b3R5cGUubWVyZ2VVcGRhdGU9ZnVuY3Rpb24oZSl7ImRhdGEiaW4gZSYmdGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2godGhpcy50cmVlLHRoaXMuZGlzcGF0Y2gsZS5kYXRhKSwib3V0c3RhbmRpbmciaW4gZSYmdGhpcy5vbmRlY2lkZShlLm91dHN0YW5kaW5nKSx0aGlzLl9fZHJhaW4oKX0sZS5wcm90b3R5cGUuX19yZWNEZWxldGVBbmREaXNwYXRjaD1mdW5jdGlvbihlLHQpe2Zvcih2YXIgbiBpbiBlKXt2YXIgcj1lW25dO2lmKEFycmF5LmlzQXJyYXkocikpe3ZhciBpPSIjIituO2kgaW4gZSYmaSBpbiB0JiZ0aGlzLl9fcmVjRGVsZXRlQW5kRGlzcGF0Y2goZVtpXSx0W2ldKX1lbHNlIG4gaW4gdCYmdGhpcy5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoKHIsdFtuXSk7dmFyIG89Ii0iK247bnVsbCE9dCYmbyBpbiB0JiZ0aGlzLl9fZmlyZSh0W29dLHtrZXk6bixiZWZvcmU6cix2YWx1ZTpudWxsfSl9fSxlLnByb3RvdHlwZS5fX3JlY01lcmdlQW5kRGlzcGF0Y2g9ZnVuY3Rpb24oZSx0LG4pe2Zvcih2YXIgciBpbiBuKXt2YXIgaT1uW3JdO2lmKG51bGwhPT1pKXt2YXIgbz0hKHIgaW4gZSk7aWYoIm9iamVjdCI9PXR5cGVvZiBpKXt2YXIgcz0iQG8iaW4gaXx8IkBzImluIGk7ciBpbiBlfHwocz8oZVtyXT1bXSxlWyIjIityXT17fSk6ZVtyXT17fSksKHM9QXJyYXkuaXNBcnJheShlW3JdKXx8cyk/dGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2hBcnJheShlW3JdLG51bGwhPXQmJnIgaW4gdD90W3JdOm51bGwsZVsiIyIrcl0saSk6dGhpcy5fX3JlY01lcmdlQW5kRGlzcGF0Y2goZVtyXSxudWxsIT10JiZyIGluIHQ/dFtyXTpudWxsLGkpfWVsc2V7dmFyIGM9ciBpbiBlP2Vbcl06bnVsbDtlW3JdPWksbnVsbCE9dCYmciBpbiB0JiZ0aGlzLl9fZmlyZSh0W3JdLHtrZXk6cixiZWZvcmU6Yyx2YWx1ZTppfSl9aWYobyl7dmFyIHU9IisiK3I7bnVsbCE9dCYmdSBpbiB0JiZ0aGlzLl9fZmlyZSh0W3VdLHtrZXk6cix2YWx1ZTplW3JdfSl9bnVsbCE9dCYmIkBlImluIHQmJnRoaXMuX19maXJlKHQse3ZhbHVlOmV9KX1lbHNle3ZhciBhPWVbcl0sZD0iLSIrcjtpZihudWxsIT10JiZkIGluIHQmJnRoaXMuX19maXJlKHRbZF0se2tleTpyLGJlZm9yZTphLHZhbHVlOm51bGx9KSxBcnJheS5pc0FycmF5KGEpKXt2YXIgaD0iIyIrcjtoIGluIGUmJmggaW4gdCYmdGhpcy5fX3JlY0RlbGV0ZUFuZERpc3BhdGNoKGVbaF0sdFtoXSksZGVsZXRlIGVbIiMiK3JdfWVsc2UgciBpbiB0JiZ0aGlzLl9fcmVjRGVsZXRlQW5kRGlzcGF0Y2goYSx0W3JdKTtkZWxldGUgZVtyXSxudWxsIT10JiZyIGluIHQmJnRoaXMuX19maXJlKHRbcl0se2tleTpyLGJlZm9yZTphLHZhbHVlOm51bGx9KX19fSxlLnByb3RvdHlwZS5fX3JlY01lcmdlQW5kRGlzcGF0Y2hBcnJheT1mdW5jdGlvbihlLHQsbixyKXt2YXIgaT1udWxsLG89bnVsbDtmb3IodmFyIHMgaW4gcilpZigiQG8iPT1zKWk9cltzXTtlbHNlIGlmKCJAcyI9PXMpbz1yW3NdO2Vsc2UgaWYobnVsbD09cltzXSl0JiYiLSJpbiB0JiYocyBpbiB0P3RoaXMuX19maXJlKHRbIi0iXSx7a2V5OnMsYmVmb3JlOm5bc10sdmFsdWU6bnVsbCxjbGVhbjohMCxjbGVhbl9vbjp0W3NdfSk6dGhpcy5fX2ZpcmUodFsiLSJdLHtrZXk6cyxiZWZvcmU6bltzXSx2YWx1ZTpudWxsfSkpLGRlbGV0ZSBuW3NdO2Vsc2V7dmFyIGM9ITE7bnVsbCE9biYmcyBpbiBufHwodCYmIisiaW4gdCYmKGM9ITApLG5bc109e30pLG5bc10uX19rZXk9IiIrcyx0aGlzLl9fcmVjTWVyZ2VBbmREaXNwYXRjaChuW3NdLG51bGwhPXQmJnMgaW4gdD90W3NdOm51bGwscltzXSksYz8ocyBpbiB0fHwodFtzXT17fSksdGhpcy5fX2ZpcmUodFsiKyJdLHtrZXk6cyxiZWZvcmU6bnVsbCx2YWx1ZTpuW3NdLGFwcGVuZF9yZXN1bHQ6ITAsYXBwZW5kX3RvOnRbc119KSk6dCYmIiEiaW4gdCYmdGhpcy5fX2ZpcmUodFsiISJdLHtrZXk6cyx2YWx1ZTpuW3NdfSl9dmFyIHU9e2JlZm9yZTplLHZhbHVlOmV9O2lmKG51bGwhPT1vKXt1LmJlZm9yZT1bXTtmb3IodmFyIGE9MDthPGUubGVuZ3RoO2ErKyl1LmJlZm9yZS5wdXNoKGVbYV0pO2UubGVuZ3RoPW99aWYobnVsbCE9PWkpe3ZhciBkPVtdO3UuYmVmb3JlPVtdO3ZhciBoPVtdO2ZvcihhPTA7YTxlLmxlbmd0aDthKyspdS5iZWZvcmUucHVzaChlW2FdKTtmb3IoYT0wO2E8aS5sZW5ndGg7YSsrKXt2YXIgcD1pW2FdLGY9dHlwZW9mIHA7aWYoInN0cmluZyI9PWZ8fCJudW1iZXIiPT1mKWQucHVzaChuW3BdKSxoLnB1c2goIiIrcCk7ZWxzZSBmb3IodmFyIGw9cFswXSxfPXBbMV0seT1sO3k8PV87eSsrKWgucHVzaChlW3ldLl9fa2V5KSxkLnB1c2goZVt5XSl9dCYmIl4iaW4gdCYmdGhpcy5fX2ZpcmUodFsiXiJdLHtuZXdfb3JkZXI6aH0pLGUubGVuZ3RoPWQubGVuZ3RoO2ZvcihhPTA7YTxkLmxlbmd0aDthKyspZVthXT1kW2FdfXRoaXMuX19maXJlKHQsdSl9LGUucHJvdG90eXBlLl9fZmlyZT1mdW5jdGlvbihlLHQpe2lmKGUmJiJAZSJpbiBlKXtmb3IodmFyIG49ZVsiQGUiXSxyPTAsaT0wO2k8bi5sZW5ndGg7aSsrKXt2YXIgbz1uW2ldO251bGwhPT1vP3RoaXMucXVldWUucHVzaCh7Y2I6by5jYixvcmRlcjpvLm9yZGVyLGNoYW5nZTp0LGRpc3BhdGNoX2xpc3Q6bixpbmRleDppfSk6cisrfWlmKHI+MCl7dmFyIHM9W107Zm9yKGk9MDtpPG4ubGVuZ3RoO2krKyludWxsIT09byYmcy5wdXNoKG8pO2VbIkBlIl09c319fSxlLnByb3RvdHlwZS5fX2RyYWluPWZ1bmN0aW9uKCl7dGhpcy5xdWV1ZS5zb3J0KChmdW5jdGlvbihlLHQpe3JldHVybiBlLm9yZGVyLXQub3JkZXJ9KSk7Zm9yKHZhciBlPTA7ZTx0aGlzLnF1ZXVlLmxlbmd0aDtlKyspe3ZhciB0PXRoaXMucXVldWVbZV0sbj10LmNiKHQuY2hhbmdlKTtpZih0LmNoYW5nZS5jbGVhbiYmdGhpcy5fX2F1dG9EZWxldGUodC5jaGFuZ2UuY2xlYW5fb24pLCJkZWxldGUiPT09bil0LmRpc3BhdGNoX2xpc3RbdC5pbmRleF09bnVsbDtlbHNlIGlmKCgiZnVuY3Rpb24iPT10eXBlb2Ygbnx8Im9iamVjdCI9PXR5cGVvZiBufHxBcnJheS5pc0FycmF5KG4pKSYmdC5jaGFuZ2UuYXBwZW5kX3Jlc3VsdClpZihBcnJheS5pc0FycmF5KG4pKWZvcihlPTA7ZTxuLmxlbmd0aDtlKyspdGhpcy5fX3JlY0FwcGVuZENoYW5nZSh0LmNoYW5nZS5hcHBlbmRfdG8sbltlXSx0Lm9yZGVyLCEwKTtlbHNlIHRoaXMuX19yZWNBcHBlbmRDaGFuZ2UodC5jaGFuZ2UuYXBwZW5kX3RvLG4sdC5vcmRlciwhMCl9dGhpcy5xdWV1ZT1bXX0sZX0oKSxhPWZ1bmN0aW9uKCl7ZnVuY3Rpb24gZShlKXt0aGlzLnByZWZpeD1lfXJldHVybiBlLnByb3RvdHlwZS5mYWlsdXJlPWZ1bmN0aW9uKGUpe2NvbnNvbGUubG9nKHRoaXMucHJlZml4KyJ8ZXJyb3IiK2UpfSxlLnByb3RvdHlwZS5zdWNjZXNzPWZ1bmN0aW9uKGUpe2NvbnNvbGUubG9nKHRoaXMucHJlZml4KyJ8c3VjY2VzcztzZXE9IitlLnNlcSl9LGV9KCksZD1mdW5jdGlvbigpe2Z1bmN0aW9uIGUoZSl7dGhpcy50cmVlPWV9cmV0dXJuIGUucHJvdG90eXBlLm5leHQ9ZnVuY3Rpb24oZSl7dGhpcy50cmVlLm1lcmdlVXBkYXRlKGUuZGVsdGEpfSxlLnByb3RvdHlwZS5mYWlsdXJlPWZ1bmN0aW9uKGUpe2NvbnNvbGUubG9nKCJ0cmVlfGZhaWx1cmU9IitlKX0sZS5wcm90b3R5cGUuY29tcGxldGU9ZnVuY3Rpb24oKXt9LGV9KCksaD1mdW5jdGlvbigpe2Z1bmN0aW9uIGUoZSl7dGhpcy5iYWNrb2ZmPTEsdGhpcy5ob3N0PWUsdGhpcy51cmw9IndzczovLyIrZSsiL3MiLHRoaXMuYXNzZXRzPSEwLHRoaXMuY29ubmVjdGVkPSExLHRoaXMuZGVhZD0hMSx0aGlzLm1heGltdW1fYmFja29mZj0yNTAwLHRoaXMuc29ja2V0PW51bGwsdGhpcy5vbnN0YXR1c2NoYW5nZT1mdW5jdGlvbihlKXt9LHRoaXMub25waW5nPWZ1bmN0aW9uKGUsdCl7fSx0aGlzLm9uYXV0aG5lZWRlZD1mdW5jdGlvbihlKXt9LHRoaXMuc2NoZWR1bGVkPSExLHRoaXMuY2FsbGJhY2tzPW5ldyBNYXAsdGhpcy5uZXh0SWQ9MCx0aGlzLm9ucmVjb25uZWN0PW5ldyBNYXAsdGhpcy5zZXNzaW9uSWQ9IiIsdGhpcy5zZW5kSWQ9MH1yZXR1cm4gZS5wcm90b3R5cGUuc3RvcD1mdW5jdGlvbigpe3RoaXMuZGVhZD0hMCxudWxsIT09dGhpcy5zb2NrZXQmJnRoaXMuc29ja2V0LmNsb3NlKCl9LGUucHJvdG90eXBlLl9yZXRyeT1mdW5jdGlvbigpe2lmKHRoaXMuc29ja2V0PW51bGwsdGhpcy5jb25uZWN0ZWQmJih0aGlzLmNvbm5lY3RlZD0hMSx0aGlzLm9uc3RhdHVzY2hhbmdlKCExKSksdGhpcy5jYWxsYmFja3MuY2xlYXIoKSwhdGhpcy5kZWFkJiYhdGhpcy5zY2hlZHVsZWQpe3ZhciBlPSExO3RoaXMuYmFja29mZis9TWF0aC5yYW5kb20oKSp0aGlzLmJhY2tvZmYsdGhpcy5iYWNrb2ZmPnRoaXMubWF4aW11bV9iYWNrb2ZmJiYodGhpcy5iYWNrb2ZmPXRoaXMubWF4aW11bV9iYWNrb2ZmLGU9ITApLHRoaXMuc2NoZWR1bGVkPSEwO3ZhciB0PXRoaXM7c2V0VGltZW91dCgoZnVuY3Rpb24oKXt0LnN0YXJ0KCl9KSx0aGlzLmJhY2tvZmYpLGUmJih0aGlzLmJhY2tvZmYvPTIpfX0sZS5wcm90b3R5cGUuc3RhcnQ9ZnVuY3Rpb24oKXt2YXIgZT10aGlzO3RoaXMuc2NoZWR1bGVkPSExLHRoaXMuZGVhZD0hMSx0aGlzLnNvY2tldD1uZXcgaS5hKHRoaXMudXJsKSx0aGlzLnNvY2tldC5vbm1lc3NhZ2U9ZnVuY3Rpb24odCl7dmFyIG49SlNPTi5wYXJzZSh0LmRhdGEpO2lmKCJwaW5nImluIG4pcmV0dXJuIGUub25waW5nKG4ucGluZyxuLmxhdGVuY3kpLG4ucG9uZz0obmV3IERhdGUpLmdldFRpbWUoKS8xZTMsdm9pZCBlLnNvY2tldC5zZW5kKEpTT04uc3RyaW5naWZ5KG4pKTtpZigic3RhdHVzImluIG4pcmV0dXJuImNvbm5lY3RlZCIhPW4uc3RhdHVzPyhlLmRlYWQ9ITAsZS5zb2NrZXQuY2xvc2UoKSxlLnNvY2tldD1udWxsLHZvaWQgZS5vbmF1dGhuZWVkZWQoKGZ1bmN0aW9uKCl7ZS5zdGFydCgpfSkpKTooZS5iYWNrb2ZmPTEsZS5jb25uZWN0ZWQ9ITAsZS5hc3NldHM9bi5hc3NldHMsZS5zZXNzaW9uSWQ9bi5zZXNzaW9uX2lkLGUub25zdGF0dXNjaGFuZ2UoITApLGUuQ29uZmlndXJlTWFrZU9yR2V0QXNzZXRLZXkoe3N1Y2Nlc3M6ZnVuY3Rpb24odCl7dHJ5e3ZhciBuPW5ldyBYTUxIdHRwUmVxdWVzdDtuLm9wZW4oIkdFVCIsImh0dHBzOi8vIitlLmhvc3QrIi9wIit0LmFzc2V0S2V5LCEwKSxuLndpdGhDcmVkZW50aWFscz0hMCxuLnNlbmQoKX1jYXRjaChlKXtjb25zb2xlLmxvZyhlKX19LGZhaWx1cmU6ZnVuY3Rpb24oKXt9fSksdm9pZCBlLl9yZWNvbm5lY3QoKSk7aWYoImZhaWx1cmUiaW4gbillLmNhbGxiYWNrcy5oYXMobi5mYWlsdXJlKSYmKHI9ZS5jYWxsYmFja3MuZ2V0KG4uZmFpbHVyZSkpJiYoZS5jYWxsYmFja3MuZGVsZXRlKG4uZmFpbHVyZSkscihuKSk7ZWxzZSBpZigiZGVsaXZlciJpbiBuKXt2YXIgcjtpZihlLmNhbGxiYWNrcy5oYXMobi5kZWxpdmVyKSkocj1lLmNhbGxiYWNrcy5nZXQobi5kZWxpdmVyKSkmJihuLmRvbmUmJmUuY2FsbGJhY2tzLmRlbGV0ZShuLmRlbGl2ZXIpLHIobikpfX0sdGhpcy5zb2NrZXQub25jbG9zZT1mdW5jdGlvbih0KXtlLl9yZXRyeSgpfSx0aGlzLnNvY2tldC5vbmVycm9yPWZ1bmN0aW9uKHQpe2UuX3JldHJ5KCl9fSxlLnByb3RvdHlwZS5fd3JpdGU9ZnVuY3Rpb24oZSx0KXt0aGlzLmNvbm5lY3RlZD8odGhpcy5jYWxsYmFja3Muc2V0KGUuaWQsdCksdGhpcy5zb2NrZXQuc2VuZChKU09OLnN0cmluZ2lmeShlKSkpOnQoe2ZhaWx1cmU6NjAwLHJlYXNvbjo5OTk5fSl9LGUucHJvdG90eXBlLndhaXRfY29ubmVjdGVkPWZ1bmN0aW9uKCl7cmV0dXJuIG8odGhpcyx2b2lkIDAsdm9pZCAwLChmdW5jdGlvbigpe3ZhciBlLHQ7cmV0dXJuIHModGhpcywoZnVuY3Rpb24obil7cmV0dXJuIHRoaXMuY29ubmVjdGVkP1syLG5ldyBQcm9taXNlKChmdW5jdGlvbihlKXtlKCEwKX0pKV06KGU9dGhpcyx0PXRoaXMub25zdGF0dXNjaGFuZ2UsWzIsbmV3IFByb21pc2UoKGZ1bmN0aW9uKG4pe2Uub25zdGF0dXNjaGFuZ2U9ZnVuY3Rpb24ocil7dChyKSxyJiYobighMCksZS5vbnN0YXR1c2NoYW5nZT10KX19KSldKX0pKX0pKX0sZS5wcm90b3R5cGUuX3JlY29ubmVjdD1mdW5jdGlvbigpe3RoaXMub25yZWNvbm5lY3QuZm9yRWFjaCgoZnVuY3Rpb24oZSx0KXtlLl9fcmV0cnkoKX0pKX0sZS5wcm90b3R5cGUuX19leGVjdXRlX3JyPWZ1bmN0aW9uKGUpe3ZhciB0PXRoaXM7cmV0dXJuIGUuZmlyc3Q9ITAsdC5fd3JpdGUoZS5yZXF1ZXN0LChmdW5jdGlvbihuKXtlLmZpcnN0JiYoZS5maXJzdD0hMSwiZmFpbHVyZSJpbiBuP2UucmVzcG9uZGVyLmZhaWx1cmUobi5yZWFzb24pOmUucmVzcG9uZGVyLnN1Y2Nlc3Mobi5yZXNwb25zZSkpLHQub25yZWNvbm5lY3QuZGVsZXRlKGUuaWQpfSkpLHQub25yZWNvbm5lY3Quc2V0KGUuaWQsZSksZS5fX3JldHJ5PWZ1bmN0aW9uKCl7dC5fX2V4ZWN1dGVfcnIoZSl9LGV9LGUucHJvdG90eXBlLl9fZXhlY3V0ZV9zdHJlYW09ZnVuY3Rpb24oZSl7dmFyIHQ9dGhpcztyZXR1cm4gdC5fd3JpdGUoZS5yZXF1ZXN0LChmdW5jdGlvbihuKXtpZigiZmFpbHVyZSJpbiBuKXJldHVybiBlLnJlc3BvbmRlci5mYWlsdXJlKG4ucmVhc29uKSx2b2lkIHQub25yZWNvbm5lY3QuZGVsZXRlKGUuaWQpO24ucmVzcG9uc2UmJmUucmVzcG9uZGVyLm5leHQobi5yZXNwb25zZSksbi5kb25lJiYoZS5yZXNwb25kZXIuY29tcGxldGUoKSx0Lm9ucmVjb25uZWN0LmRlbGV0ZShlLmlkKSl9KSksdC5vbnJlY29ubmVjdC5zZXQoZS5pZCxlKSxlLl9fcmV0cnk9ZnVuY3Rpb24oKXt0Ll9fZXhlY3V0ZV9zdHJlYW0oZSl9LGV9LGUucHJvdG90eXBlLkluaXRTZXR1cEFjY291bnQ9ZnVuY3Rpb24oZSx0KXt0aGlzLm5leHRJZCsrO3ZhciBuPXRoaXMubmV4dElkO3JldHVybiB0aGlzLl9fZXhlY3V0ZV9ycih7aWQ6bixyZXNwb25kZXI6dCxyZXF1ZXN0OnttZXRob2Q6ImluaXQvc2V0dXAtYWNjb3VudCIsaWQ6bixlbWFpbDplfX0pfSxlLnByb3RvdHlwZS5Jbml0Q29tcGxldGVBY2NvdW50PWZ1bmN0aW9uKGUsdCxuLHIpe3RoaXMubmV4dElkKys7dmFyIGk9dGhpcy5uZXh0SWQ7cmV0dXJuIHRoaXMuX19leGVjdXRlX3JyKHtpZDppLHJlc3BvbmRlcjpyLHJlcXVlc3Q6e21ldGhvZDoiaW5pdC9jb21wbGV0ZS1hY2NvdW50IixpZDppLGVtYWlsOmUscmV2b2tlOnQsY29kZTpufX0pfSxlLnByb3RvdHlwZS5BY2NvdW50U2V0UGFzc3dvcmQ9ZnVuY3Rpb24oZSx0LG4pe3RoaXMubmV4dElkKys7dmFyIHI9dGhpcy5uZXh0SWQ7cmV0dXJuIHRoaXMuX19leGVjdXRlX3JyKHtpZDpyLHJlc3BvbmRlcjpuLHJlcXVlc3Q6e21ldGhvZDoiYWNjb3VudC9zZXQtcGFzc3dvcmQiLGlkOnIsaWRlbnRpdHk6ZSxwYXNzd29yZDp0fX0pfSxlLnByb3RvdHlwZS5BY2NvdW50TG9naW49ZnVuY3Rpb24oZSx0LG4pe3RoaXMubmV4dElkKys7dmFyIHI9dGhpcy5uZXh0SWQ7cmV0dXJuIHRoaXMuX19leGVjdXRlX3JyKHtpZDpyLHJlc3BvbmRlcjpuLHJlcXVlc3Q6e21ldGhvZDoiYWNjb3VudC9sb2dpbiIsaWQ6cixlbWFpbDplLHBhc3N3b3JkOnR9fSl9LGUucHJvdG90eXBlLlByb2JlPWZ1bmN0aW9uKGUsdCl7dGhpcy5uZXh0SWQrKzt2YXIgbj10aGlzLm5leHRJZDtyZXR1cm4gdGhpcy5fX2V4ZWN1dGVfcnIoe2lkOm4scmVzcG9uZGVyOnQscmVxdWVzdDp7bWV0aG9kOiJwcm9iZSIsaWQ6bixpZGVudGl0eTplfX0pfSxlLnByb3RvdHlwZS5BdXRob3JpdHlDcmVhdGU9ZnVuY3Rpb24oZSx0KXt0aGlzLm5leHRJZCsrO3ZhciBuPXRoaXMubmV4dElkO3JldHVybiB0aGlzLl9fZXhlY3V0ZV9ycih7aWQ6bixyZXNwb25kZXI6dCxyZXF1ZXN0OnttZXRob2Q6ImF1dGhvcml0eS9jcmVhdGUiLGlkOm4saWRlbnRpdHk6ZX19KX0sZS5wcm90b3R5cGUuQXV0aG9yaXR5U2V0PWZ1bmN0aW9uKGUsdCxuLHIpe3RoaXMubmV4dElkKys7dmFyIGk9dGhpcy5uZXh0SWQ7cmV0dXJuIHRoaXMuX19leGVjdXRlX3JyKHtpZDppLHJlc3BvbmRlcjpyLHJlcXVlc3Q6e21ldGhvZDoiYXV0aG9yaXR5L3NldCIsaWQ6aSxpZGVudGl0eTplLGF1dGhvcml0eTp0LCJrZXktc3RvcmUiOm59fSl9LGUucHJvdG90eXBlLkF1dGhvcml0eUdldD1mdW5jdGlvbihlLHQsbil7dGhpcy5uZXh0SWQrKzt2YXIgcj10aGlzLm5leHRJZDtyZXR1cm4gdGhpcy5fX2V4ZWN1dGVfcnIoe2lkOnIscmVzcG9uZGVyOm4scmVxdWVzdDp7bWV0aG9kOiJhdXRob3JpdHkvZ2V0IixpZDpyLGlkZW50aXR5OmUsYXV0aG9yaXR5OnR9fSl9LGUucHJvdG90eXBlLkF1dGhvcml0eUxpc3Q9ZnVuY3Rpb24oZSx0KXt0aGlzLm5leHRJZCsrO3ZhciBuPXRoaXMubmV4dElkO3JldHVybiB0aGlzLl9fZXhlY3V0ZV9zdHJlYW0oe2lkOm4scmVzcG9uZGVyOnQscmVxdWVzdDp7bWV0aG9kOiJhdXRob3JpdHkvbGlzdCIsaWQ6bixpZGVudGl0eTplfX0pfSxlLnByb3RvdHlwZS5BdXRob3JpdHlEZXN0cm95PWZ1bmN0aW9uKGUsdCxuKXt0aGlzLm5leHRJZCsrO3ZhciByPXRoaXMubmV4dElkO3JldHVybiB0aGlzLl9fZXhlY3V0ZV9ycih7aWQ6cixyZXNwb25kZXI6bixyZXF1ZXN0OnttZXRob2Q6ImF1dGhvcml0eS9kZXN0cm95IixpZDpyLGlkZW50aXR5OmUsYXV0aG9yaXR5OnR9fSl9LGUucHJvdG90eXBlLlNwYWNlQ3JlYXRlPWZ1bmN0aW9uKGUsdCxuKXt0aGlzLm5leHRJZCsrO3ZhciByPXRoaXMubmV4dElkO3JldHVybiB0aGlzLl9fZXhlY3V0ZV9ycih7aWQ6cixyZXNwb25kZXI6bixyZXF1ZXN0OnttZXRob2Q6InNwYWNlL2NyZWF0ZSIsaWQ6cixpZGVudGl0eTplLHNwYWNlOnR9fSl9LGUucHJvdG90eXBlLlNwYWNlVXNhZ2U9ZnVuY3Rpb24oZSx0LG4scil7dGhpcy5uZXh0SWQrKzt2YXIgaT10aGlzLm5leHRJZDtyZXR1cm4gdGhpcy5fX2V4ZWN1dGVfc3RyZWFtKHtpZDppLHJlc3BvbmRlcjpyLHJlcXVlc3Q6e21ldGhvZDoic3BhY2UvdXNhZ2UiLGlkOmksaWRlbnRpdHk6ZSxzcGFjZTp0LGxpbWl0Om59fSl9LGUucHJvdG90eXBlLlNwYWNlR2V0PWZ1bmN0aW9uKGUsdCxuKXt0aGlzLm5leHRJZCsrO3ZhciByPXRoaXMubmV4dElkO3JldHVybiB0aGlzLl9fZXhlY3V0ZV9ycih7aWQ6cixyZXNwb25kZXI6bixyZXF1ZXN0OnttZXRob2Q6InNwYWNlL2dldCIsaWQ6cixpZGVudGl0eTplLHNwYWNlOnR9fSl9LGUucHJvdG90eXBlLlNwYWNlU2V0PWZ1bmN0aW9uKGUsdCxuLHIpe3RoaXMubmV4dElkKys7dmFyIGk9dGhpcy5uZXh0SWQ7cmV0dXJuIHRoaXMuX19leGVjdXRlX3JyKHtpZDppLHJlc3BvbmRlcjpyLHJlcXVlc3Q6e21ldGhvZDoic3BhY2Uvc2V0IixpZDppLGlkZW50aXR5OmUsc3BhY2U6dCxwbGFuOm59fSl9LGUucHJvdG90eXBlLlNwYWNlRGVsZXRlPWZ1bmN0aW9uKGUsdCxuKXt0aGlzLm5leHRJZCsrO3ZhciByPXRoaXMubmV4dElkO3JldHVybiB0aGlzLl9fZXhlY3V0ZV9ycih7aWQ6cixyZXNwb25kZXI6bixyZXF1ZXN0OnttZXRob2Q6InNwYWNlL2RlbGV0ZSIsaWQ6cixpZGVudGl0eTplLHNwYWNlOnR9fSl9LGUucHJvdG90eXBlLlNwYWNlU2V0Um9sZT1mdW5jdGlvbihlLHQsbixyLGkpe3RoaXMubmV4dElkKys7dmFyIG89dGhpcy5uZXh0SWQ7cmV0dXJuIHRoaXMuX19leGVjdXRlX3JyKHtpZDpvLHJlc3BvbmRlcjppLHJlcXVlc3Q6e21ldGhvZDoic3BhY2Uvc2V0LXJvbGUiLGlkOm8saWRlbnRpdHk6ZSxzcGFjZTp0LGVtYWlsOm4scm9sZTpyfX0pfSxlLnByb3RvdHlwZS5TcGFjZVJlZmxlY3Q9ZnVuY3Rpb24oZSx0LG4scil7dGhpcy5uZXh0SWQrKzt2YXIgaT10aGlzLm5leHRJZDtyZXR1cm4gdGhpcy5fX2V4ZWN1dGVfcnIoe2lkOmkscmVzcG9uZGVyOnIscmVxdWVzdDp7bWV0aG9kOiJzcGFjZS9yZWZsZWN0IixpZDppLGlkZW50aXR5OmUsc3BhY2U6dCxrZXk6bn19KX0sZS5wcm90b3R5cGUuU3BhY2VMaXN0PWZ1bmN0aW9uKGUsdCxuLHIpe3RoaXMubmV4dElkKys7dmFyIGk9dGhpcy5uZXh0SWQ7cmV0dXJuIHRoaXMuX19leGVjdXRlX3N0cmVhbSh7aWQ6aSxyZXNwb25kZXI6cixyZXF1ZXN0OnttZXRob2Q6InNwYWNlL2xpc3QiLGlkOmksaWRlbnRpdHk6ZSxtYXJrZXI6dCxsaW1pdDpufX0pfSxlLnByb3RvdHlwZS5Eb2N1bWVudENyZWF0ZT1mdW5jdGlvbihlLHQsbixyLGksbyl7dGhpcy5uZXh0SWQrKzt2YXIgcz10aGlzLm5leHRJZDtyZXR1cm4gdGhpcy5fX2V4ZWN1dGVfcnIoe2lkOnMscmVzcG9uZGVyOm8scmVxdWVzdDp7bWV0aG9kOiJkb2N1bWVudC9jcmVhdGUiLGlkOnMsaWRlbnRpdHk6ZSxzcGFjZTp0LGtleTpuLGVudHJvcHk6cixhcmc6aX19KX0sZS5wcm90b3R5cGUuRG9jdW1lbnRMaXN0PWZ1bmN0aW9uKGUsdCxuLHIsaSl7dGhpcy5uZXh0SWQrKzt2YXIgbz10aGlzLm5leHRJZDtyZXR1cm4gdGhpcy5fX2V4ZWN1dGVfc3RyZWFtKHtpZDpvLHJlc3BvbmRlcjppLHJlcXVlc3Q6e21ldGhvZDoiZG9jdW1lbnQvbGlzdCIsaWQ6byxpZGVudGl0eTplLHNwYWNlOnQsbWFya2VyOm4sbGltaXQ6cn19KX0sZS5wcm90b3R5cGUuQ29ubmVjdGlvbkNyZWF0ZT1mdW5jdGlvbihlLHQsbixyLGkpe3ZhciBvPXRoaXM7by5uZXh0SWQrKzt2YXIgcz1vLm5leHRJZDtyZXR1cm4gby5fX2V4ZWN1dGVfc3RyZWFtKHtpZDpzLHJlc3BvbmRlcjppLHJlcXVlc3Q6e21ldGhvZDoiY29ubmVjdGlvbi9jcmVhdGUiLGlkOnMsaWRlbnRpdHk6ZSxzcGFjZTp0LGtleTpuLCJ2aWV3ZXItc3RhdGUiOnJ9LHNlbmQ6ZnVuY3Rpb24oZSx0LG4pe28ubmV4dElkKys7dmFyIHI9by5uZXh0SWQ7by5fX2V4ZWN1dGVfcnIoe2lkOnIscmVzcG9uZGVyOm4scmVxdWVzdDp7bWV0aG9kOiJjb25uZWN0aW9uL3NlbmQiLGlkOnIsY29ubmVjdGlvbjpzLGNoYW5uZWw6ZSxtZXNzYWdlOnR9fSl9LHVwZGF0ZTpmdW5jdGlvbihlLHQpe28ubmV4dElkKys7dmFyIG49by5uZXh0SWQ7by5fX2V4ZWN1dGVfcnIoe2lkOm4scmVzcG9uZGVyOnQscmVxdWVzdDp7bWV0aG9kOiJjb25uZWN0aW9uL3VwZGF0ZSIsaWQ6bixjb25uZWN0aW9uOnMsInZpZXdlci1zdGF0ZSI6ZX19KX0sZW5kOmZ1bmN0aW9uKGUpe28ubmV4dElkKys7dmFyIHQ9by5uZXh0SWQ7by5fX2V4ZWN1dGVfcnIoe2lkOnQscmVzcG9uZGVyOmUscmVxdWVzdDp7bWV0aG9kOiJjb25uZWN0aW9uL2VuZCIsaWQ6dCxjb25uZWN0aW9uOnN9fSl9fSl9LGUucHJvdG90eXBlLkNvbmZpZ3VyZU1ha2VPckdldEFzc2V0S2V5PWZ1bmN0aW9uKGUpe3RoaXMubmV4dElkKys7dmFyIHQ9dGhpcy5uZXh0SWQ7cmV0dXJuIHRoaXMuX19leGVjdXRlX3JyKHtpZDp0LHJlc3BvbmRlcjplLHJlcXVlc3Q6e21ldGhvZDoiY29uZmlndXJlL21ha2Utb3ItZ2V0LWFzc2V0LWtleSIsaWQ6dH19KX0sZS5wcm90b3R5cGUuQXR0YWNobWVudFN0YXJ0PWZ1bmN0aW9uKGUsdCxuLHIsaSxvKXt2YXIgcz10aGlzO3MubmV4dElkKys7dmFyIGM9cy5uZXh0SWQ7cmV0dXJuIHMuX19leGVjdXRlX3N0cmVhbSh7aWQ6YyxyZXNwb25kZXI6byxyZXF1ZXN0OnttZXRob2Q6ImF0dGFjaG1lbnQvc3RhcnQiLGlkOmMsaWRlbnRpdHk6ZSxzcGFjZTp0LGtleTpuLGZpbGVuYW1lOnIsImNvbnRlbnQtdHlwZSI6aX0sYXBwZW5kOmZ1bmN0aW9uKGUsdCxuKXtzLm5leHRJZCsrO3ZhciByPXMubmV4dElkO3MuX19leGVjdXRlX3JyKHtpZDpyLHJlc3BvbmRlcjpuLHJlcXVlc3Q6e21ldGhvZDoiYXR0YWNobWVudC9hcHBlbmQiLGlkOnIsdXBsb2FkOmMsImNodW5rLW1kNSI6ZSwiYmFzZTY0LWJ5dGVzIjp0fX0pfSxmaW5pc2g6ZnVuY3Rpb24oZSl7cy5uZXh0SWQrKzt2YXIgdD1zLm5leHRJZDtzLl9fZXhlY3V0ZV9ycih7aWQ6dCxyZXNwb25kZXI6ZSxyZXF1ZXN0OnttZXRob2Q6ImF0dGFjaG1lbnQvZmluaXNoIixpZDp0LHVwbG9hZDpjfX0pfX0pfSxlfSgpfSxmdW5jdGlvbihlLHQpe3ZhciBuO249ZnVuY3Rpb24oKXtyZXR1cm4gdGhpc30oKTt0cnl7bj1ufHxuZXcgRnVuY3Rpb24oInJldHVybiB0aGlzIikoKX1jYXRjaChlKXsib2JqZWN0Ij09dHlwZW9mIHdpbmRvdyYmKG49d2luZG93KX1lLmV4cG9ydHM9bn1dKX0pKTs=");
}
