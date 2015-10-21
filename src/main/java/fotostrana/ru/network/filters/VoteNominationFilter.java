package fotostrana.ru.network.filters;

import fotostrana.ru.ParserJSON;

/**
 * проверяет ответ голосования в JSON формате и получает информацию о количестве
 * голосов и месте в голосовании
 * 
 */
public class VoteNominationFilter extends Filter {

	@Override
	public boolean filtrate(String result) {
		int succes = ParserJSON.getInt(result, "success");
		return (succes >= 1);
	}

}
