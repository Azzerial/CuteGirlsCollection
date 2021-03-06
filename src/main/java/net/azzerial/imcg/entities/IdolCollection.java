package net.azzerial.imcg.entities;

import net.azzerial.cgc.database.DatabaseUserManager;
import net.azzerial.imcg.entities.utils.IdolType;
import net.azzerial.imcg.idols.core.IdolsList;
import net.azzerial.imcg.entities.utils.Progress;
import net.azzerial.imcg.entities.utils.SkinData;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class IdolCollection {

	private HashMap<Integer, Collection> collections;

	public IdolCollection(HashMap<Integer, Collection> collections) {
		this.collections = collections;
	}

	public static IdolCollection createNewEmptyCollection(long userId) {
		List<Idol> idols = IdolsList.getIdols();
		Builder idolCollectionBuilder = new Builder();

		for (int i = 0; i < idols.size(); i += 1) {
			Idol idol = idols.get(i);
			List<IdolSkin> skins = idol.getSkins();
			Collection.Builder collectionBuilder = new Collection.Builder();

			collectionBuilder.setUserId(userId);
			collectionBuilder.setIdolId(idol.getId());
			collectionBuilder.isIdolOwned(false);
			for (int n = 0; n < skins.size(); n++) {
				collectionBuilder.addSkin(
					skins.get(n).getId(),
					new SkinData(n, 0, 0)
				);
			}
			idolCollectionBuilder.addIdol(idol.getId(), collectionBuilder.build());
		}
		return (idolCollectionBuilder.build());
	}

	public Collection getCollection(int id) {
		if (!collections.containsKey(id)) {
			return (null);
		}
		return (collections.get(id));
	}

	public Collection getCollection(String idolName) {
		if (idolName == null || idolName.isEmpty()) {
			return (null);
		}
		if (idolName.contains("_")) {
			idolName = idolName.replaceAll("_", " ");
		}
		return (getCollection(IdolsList.getIdol(idolName)));
	}

	public Collection getCollection(Idol idol) {
		if (idol == null) {
			 return (null);
		}
		return (getCollection(idol.getId()));
	}

	public int getCollectionsSize() {
		return (collections.size());
	}

	public Progress getCollectionsCardsProgress() {
		int size = 0;
		int progress = 0;
		Object[] keys = collections.keySet().toArray();

		for (int i = 0; i < keys.length; i += 1) {
			Collection collection = collections.get(keys[i]);
			progress += collection.getCollectionProgress().getProgress();
			size += collection.getCollectionProgress().getSize();
		}
		return (new Progress(progress, size));
	}

	public Progress getCollectionsBasicCardProgress() {
		int size = 0;
		int progress = 0;
		Object[] keys = collections.keySet().toArray();

		for (int i = 0; i < keys.length; i += 1) {
			Collection collection = collections.get(keys[i]);
			progress += collection.getCollectionBasicCardProgress().getProgress();
			size += collection.getCollectionBasicCardProgress().getSize();
		}
		return (new Progress(progress, size));
	}

	public Progress getCollectionsEvolvedCardProgress() {
		int size = 0;
		int progress = 0;
		Object[] keys = collections.keySet().toArray();

		for (int i = 0; i < keys.length; i += 1) {
			Collection collection = collections.get(keys[i]);
			progress += collection.getCollectionEvolvedCardProgress().getProgress();
			size += collection.getCollectionEvolvedCardProgress().getSize();
		}
		return (new Progress(progress, size));
	}

	public List<Idol> getOwnedIdols() {
		List<Idol> idols = new ArrayList<Idol>();
		Object[] keys = collections.keySet().toArray();

		for (int i = 0; i < keys.length; i += 1) {
			Collection collection = collections.get(keys[i]);

			if (collection.isIdolOwned()) {
				idols.add(collection.getIdol());
			}
		}
		return (idols);
	}

	public int getScore() {
		int score = 0;
		List<Idol> idols = getOwnedIdols();

		for (int i = 0; i < idols.size(); i++) {
			Idol idol = idols.get(i);
			Collection collection = getCollection(idol);

			if (collection.isIdolOwned()) {
				List<IdolSkin> skins = idol.getSkins();
				score += idol.getIdolTier().getScoreValue() * 5;

				for (int j = 0; j < skins.size(); j += 1) {
					IdolSkin skin = skins.get(j);
					SkinData skinData = collection.getSkinData(skin);

					if (skinData.hasBasicSkin()) {
						score += (skin.getRarity().getValue() * 5) + skinData.getBasicSkinCount();
					}
					if (skinData.hasEvolvedSkin()) {
						score += (skin.getRarity().getValue() * 5 * 1.5) + skinData.getEvolvedSkinCount();
					}
				}
			}
		}
		return (score);
	}

	public Progress getCollectionsIdolsProgress() {
		return (new Progress(getOwnedIdols().size(), IdolsList.getIdols().size()));
	}

	public Progress getCollectionsIdolsByTypeProgress(IdolType idolType) {
		List<Idol> idols = new ArrayList<Idol>();

		getOwnedIdols().forEach(idol -> {
			if (idol.getIdolType().equals(idolType)) {
				idols.add(idol);
			}
		});
		return (new Progress(idols.size(), IdolsList.getIdolsByType(idolType).size()));
	}

	public static class Builder {

		private HashMap<Integer, Collection> idolsCollection = new HashMap<Integer, Collection>();

		public IdolCollection build() {
			return (new IdolCollection(idolsCollection));
		}

		public Builder addIdol(int idolId, Collection collection) {
			if (idolsCollection.containsKey(idolId)) {
				return (this);
			}
			idolsCollection.put(idolId, collection);
			return (this);
		}

	}

	public static class Collection {

		private final long userId;
		private final int idolId;
		private boolean idolOwned;
		private final HashMap<Integer, SkinData> skins;

		public Collection(long userId, int idolId, boolean idolOwned, HashMap<Integer, SkinData> skins) {
			this.userId = userId;
			this.idolId = idolId;
			this.idolOwned = idolOwned;
			this.skins = skins;
		}

		public long getUserId() {
			return (userId);
		}

		public Idol getIdol() {
			return (IdolsList.getIdol(idolId));
		}

		public int getIdolId() {
			return (idolId);
		}

		public boolean isIdolOwned() {
			return (idolOwned);
		}

		public SkinData getSkinData(int id) {
			if (!skins.containsKey(id)) {
				return (null);
			}
			return (skins.get(id));
		}

		public SkinData getSkinData(IdolSkin skin) {
			if (skin == null) {
				return (null);
			}
			return (getSkinData(skin.getId()));
		}

		public List<SkinData> getOwnedSkinData() {
			List<SkinData> skinData = new ArrayList<SkinData>();
			Object[] keys = skins.keySet().toArray();

			for (int i = 0; i < keys.length; i += 1) {
				SkinData skin = skins.get(keys[i]);
				if (skin.hasBasicSkin() || skin.hasEvolvedSkin()) {
					skinData.add(skin);
				}
			}
			return (skinData);
		}

		public Progress getCollectionProgress() {
			int progress = 0;
			Object[] keys = skins.keySet().toArray();

			for (int i = 0; i < keys.length; i += 1) {
				SkinData skin = skins.get(keys[i]);
				if (skin.hasBasicSkin()) {
					progress += 1;
				}
				if (skin.hasEvolvedSkin()) {
					progress += 1;
				}
			}
			return (new Progress(progress, skins.size() * 2));
		}

		public Progress getCollectionBasicCardProgress() {
			int progress = 0;
			Object[] keys = skins.keySet().toArray();

			for (int i = 0; i < keys.length; i += 1) {
				SkinData skin = skins.get(keys[i]);
				if (skin.hasBasicSkin()) {
					progress += 1;
				}
			}
			return (new Progress(progress, skins.size()));
		}

		public Progress getCollectionEvolvedCardProgress() {
			int progress = 0;
			Object[] keys = skins.keySet().toArray();

			for (int i = 0; i < keys.length; i += 1) {
				SkinData skin = skins.get(keys[i]);
				if (skin.hasEvolvedSkin()) {
					progress += 1;
				}
			}
			return (new Progress(progress, skins.size()));
		}

		public boolean isCollectionCompleted() {
			if (getCollectionProgress().getMissingProgress() == 0) {
				return (true);
			}
			return (false);
		}

		private String convertToString() {
			StringBuilder builder = new StringBuilder();
			Object[] keys = skins.keySet().toArray();

			builder.append(idolOwned ? "T" : "F").append(";");
			for (int i = 0; i < keys.length; i += 1) {
				SkinData skin = skins.get(keys[i]);

				builder.append(i)
					.append(":")
					.append(skin.getBasicSkinCount())
					.append("-")
					.append(skin.getEvolvedSkinCount());
				if (i + 1 < keys.length) {
					builder.append(",");
				}
			}
			return (builder.toString());
		}

		public boolean updateSkinData(SkinData skinData, boolean evolvedSkin, int value) {
			if (evolvedSkin) {
				skinData.updateEvolvedSkinCount(value);
			} else {
				skinData.updateBasicSkinCount(value);
			}
			return (DatabaseUserManager.updateUserIdolCollection(userId, IdolsList.getIdol(idolId).getDatabaseName(), convertToString()));
		}

		public boolean updateIdolOwn(boolean idolOwned) {
			this.idolOwned = idolOwned;
			return (DatabaseUserManager.updateUserIdolCollection(userId, IdolsList.getIdol(idolId).getDatabaseName(), convertToString()));
		}

		public static class Builder {

			private long userId;
			private int idolId;
			private boolean idolOwned;
			private HashMap<Integer, SkinData> skinsCount = new HashMap<Integer, SkinData>();

			public Collection build() {
				return (new Collection(userId, idolId, idolOwned, skinsCount));
			}

			public Builder setUserId(long userId) {
				this.userId = userId;
				return (this);
			}

			public Builder setIdolId(int idolId) {
				this.idolId = idolId;
				return (this);
			}

			public Builder isIdolOwned(boolean idolOwned) {
				this.idolOwned = idolOwned;
				return (this);
			}

			public Builder addSkin(int skinId, SkinData skinData) {
				if (skinsCount.containsKey(skinId) || skinData == null) {
					return (this);
				}
				skinsCount.put(skinId, skinData);
				return (this);
			}

		}

	}

}
