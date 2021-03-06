package toolbox.client.models;

import java.util.Collection;
import java.util.Map;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.base.Function;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;

import api.materials.AdornmentMaterial;
import api.materials.HandleMaterial;
import api.materials.HeadMaterial;
import api.materials.Materials;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.block.model.ItemOverride;
import net.minecraft.client.renderer.block.model.ItemOverrideList;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.resources.IResourceManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.client.model.ICustomModelLoader;
import net.minecraftforge.client.model.IModel;
import net.minecraftforge.client.model.ItemLayerModel;
import net.minecraftforge.client.model.PerspectiveMapWrapper;
import net.minecraftforge.client.model.SimpleModelState;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;
import toolbox.Toolbox;
import toolbox.common.items.ModItems;
import toolbox.common.items.tools.IAdornedTool;
import toolbox.common.items.tools.IBladeTool;
import toolbox.common.items.tools.IHandleTool;
import toolbox.common.materials.ModMaterials;

public class DaggerModel implements IModel {
	
	public static final ModelResourceLocation LOCATION = new ModelResourceLocation(
			new ResourceLocation(Toolbox.MODID, "dagger"), "inventory");

	public static final IModel MODEL = new DaggerModel();

	@Nullable
	private final ResourceLocation bladeTexture;
	@Nullable
	private final ResourceLocation handleTexture;
	@Nullable
	private final ResourceLocation adornmentTexture;
	
	public DaggerModel() {
		bladeTexture = null;
		handleTexture = null;
		adornmentTexture = null;
	}
	
	public DaggerModel(ResourceLocation blade, ResourceLocation handle, ResourceLocation adornment) {
		this.bladeTexture = blade;
		this.handleTexture = handle;
		this.adornmentTexture = adornment;
	}
	
	@Override
	public IModel retexture(ImmutableMap<String, String> textures) {
		ResourceLocation blade = null;
		ResourceLocation crossguard = null;
		ResourceLocation handle = null;
		ResourceLocation adornment = null;
		if (textures.containsKey("blade")) {
			blade = new ResourceLocation(Materials.head_registry.get(textures.get("blade")).getModId(), "items/dagger/blade_" + textures.get("blade"));
		}
		if (textures.containsKey("handle")) {
			handle = new ResourceLocation(Materials.handle_registry.get(textures.get("handle")).getModId(), "items/dagger/handle_" + textures.get("handle"));
		}
		if (textures.containsKey("adornment")) {
			adornment = new ResourceLocation(Materials.adornment_registry.get(textures.get("adornment")).getModId(), "items/dagger/adornment_" + textures.get("adornment"));
		}
		return new DaggerModel(blade, handle, adornment);
	}
	
	@Override
	public IModel process(ImmutableMap<String, String> customData) {
		ResourceLocation blade = null;
		ResourceLocation crossguard = null;
		ResourceLocation handle = null;
		ResourceLocation adornment = null;
		if (customData.containsKey("blade")) {
			blade = new ResourceLocation(Materials.head_registry.get(customData.get("blade")).getModId(), "items/dagger/blade_" + customData.get("blade"));
		}
		if (customData.containsKey("handle")) {
			handle = new ResourceLocation(Materials.handle_registry.get(customData.get("handle")).getModId(), "items/dagger/handle_" + customData.get("handle"));
		}
		if (customData.containsKey("adornment")) {
			adornment = new ResourceLocation(Materials.adornment_registry.get(customData.get("adornment")).getModId(), "items/dagger/adornment_" + customData.get("adornment"));
		}
		return new DaggerModel(blade, handle, adornment);
	}
	
	@Override
	public Collection<ResourceLocation> getDependencies() {
		return ImmutableList.of();
	}
	
	@Override
	public Collection<ResourceLocation> getTextures() {
		
		ImmutableList.Builder<ResourceLocation> builder = ImmutableList.builder();
		for (HeadMaterial mat : Materials.head_registry.values()) {
			builder.add(new ResourceLocation(mat.getModId(), "items/dagger/blade_" + mat.getName()));
		}
		for (HandleMaterial mat : Materials.handle_registry.values()) {
			builder.add(new ResourceLocation(mat.getModId(), "items/dagger/handle_" + mat.getName()));
		}
		for (AdornmentMaterial mat : Materials.adornment_registry.values()) {
			builder.add(new ResourceLocation(mat.getModId(), "items/dagger/adornment_" + mat.getName()));
		}
		Collection textures = builder.build();
		return textures;
		
	}
	
	@Override
	public IBakedModel bake(IModelState state, VertexFormat format,
			java.util.function.Function<ResourceLocation, TextureAtlasSprite> bakedTextureGetter) {

		ImmutableMap<TransformType, TRSRTransformation> transformMap = PerspectiveMapWrapper.getTransforms(state);

		TRSRTransformation transform = (TRSRTransformation.identity());

		ImmutableList.Builder<BakedQuad> builder = ImmutableList.builder();

		if (bladeTexture != null && handleTexture != null) {
			
			ImmutableList.Builder<ResourceLocation> texBuilder = ImmutableList.builder();
			if (bladeTexture != null) {
				texBuilder.add(bladeTexture);
			}
			if (handleTexture != null) {
				texBuilder.add(handleTexture);
			}
			if (adornmentTexture != null) {
				texBuilder.add(adornmentTexture);
			}
			ImmutableList<ResourceLocation> textures = texBuilder.build();
			
			IBakedModel model = (new ItemLayerModel(textures)).bake(state, format, bakedTextureGetter);
			builder.addAll(model.getQuads(null, null, 0));
		}

		return new BakedDaggerModel(this, builder.build(), format, Maps.immutableEnumMap(transformMap),
				Maps.<String, IBakedModel>newHashMap());
	}

	@Override
	public IModelState getDefaultState() {
		return TRSRTransformation.identity();
	}
	
	public enum LoaderDagger implements ICustomModelLoader {
		INSTANCE;

		@Override
		public boolean accepts(ResourceLocation modelLocation) {
			return modelLocation.getResourceDomain().equals(Toolbox.MODID)
					&& modelLocation.getResourcePath().equals("dagger");
		}

		@Override
		public IModel loadModel(ResourceLocation modelLocation) {
			return MODEL;
		}

		@Override
		public void onResourceManagerReload(IResourceManager resourceManager) {

		}
	}
	
	private static final class BakedDaggerOverrideHandler extends ItemOverrideList {
		public static final BakedDaggerOverrideHandler INSTANCE = new BakedDaggerOverrideHandler();

		private BakedDaggerOverrideHandler() {
			super(ImmutableList.<ItemOverride>of());
		}

		@Override
		@Nonnull
		public IBakedModel handleItemState(@Nonnull IBakedModel originalModel, @Nonnull ItemStack stack,
				@Nullable World world, @Nullable EntityLivingBase entity) {

			if (stack.getItem() != ModItems.dagger) {
				return originalModel;
			}

			BakedDaggerModel model = (BakedDaggerModel) originalModel;

			String key = IBladeTool.getBladeMat(stack).getName() + "|"
					+ IHandleTool.getHandleMat(stack).getName() + "|"
					+ IAdornedTool.getAdornmentMat(stack).getName();

			if (!model.cache.containsKey(key)) {
				ImmutableMap.Builder<String, String> builder = ImmutableMap.builder();
				builder.put("blade", IBladeTool.getBladeMat(stack).getName());
				builder.put("handle", IHandleTool.getHandleMat(stack).getName());
				if (IAdornedTool.getAdornmentMat(stack) != ModMaterials.ADORNMENT_NULL) {
					builder.put("adornment", IAdornedTool.getAdornmentMat(stack).getName());
				}
				IModel parent = model.parent.retexture(builder.build());
				Function<ResourceLocation, TextureAtlasSprite> textureGetter;
				textureGetter = new Function<ResourceLocation, TextureAtlasSprite>() {
					public TextureAtlasSprite apply(ResourceLocation location) {
						return Minecraft.getMinecraft().getTextureMapBlocks().getAtlasSprite(location.toString());
					}
				};
				IBakedModel bakedModel = parent.bake(new SimpleModelState(model.transforms), model.format,
						textureGetter);
				model.cache.put(key, bakedModel);
				return bakedModel;
			}

			return model.cache.get(key);
		}

	}
	
	private static final class BakedDaggerModel extends BakedToolModel {

		public BakedDaggerModel(DaggerModel parent, ImmutableList<BakedQuad> quads, VertexFormat format,
				ImmutableMap<ItemCameraTransforms.TransformType, TRSRTransformation> transforms,
				Map<String, IBakedModel> cache) {
			super(parent, quads, format, transforms, cache);
		}

		@Override
		public ItemOverrideList getOverrides() {
			return BakedDaggerOverrideHandler.INSTANCE;
		}
		
	}

}
