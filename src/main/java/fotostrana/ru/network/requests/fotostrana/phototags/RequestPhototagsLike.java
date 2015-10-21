package fotostrana.ru.network.requests.fotostrana.phototags;

import fotostrana.ru.network.requests.fotostrana.rating.RequestPhotoLike;

public class RequestPhototagsLike extends RequestPhotoLike {
	private RequestVotePhototags voteRequest;

	public RequestPhototagsLike(RequestVotePhototags parrentRequest) {
		super(parrentRequest);
		voteRequest = parrentRequest;
	}

	@Override
	protected String getData() {
		String value = "photoId="
				+ voteRequest.photoId
				+ "&ownerId="
				+ voteRequest.targetId
				+ "&pageSource=tlen-btn&addPhotoView%5Bimg%5D="
				+ voteRequest.photoId
				+ "&addPhotoView%5Baction%5D=init&addPhotoView%5Bsource%5D=1&addPhotoView%5BalbumId%5D="
				+ voteRequest.albumId
				+ "&from=phototags&additionalData%5BactionStat%5D=tlen-btn&additionalData%5BstatSentId%5D=1&additionalData%5Bsuggested%5D=0&_fs2ajax=1";
		return value;
	}

}
