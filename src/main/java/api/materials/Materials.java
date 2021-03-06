package api.materials;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import net.minecraft.item.ItemStack;


public class Materials {
	
	private static Random rand = new Random();

	public static Map<String, HeadMaterial> head_registry = new HashMap<>();
	public static Map<String, HaftMaterial> haft_registry = new HashMap<>();
	public static Map<String, HandleMaterial> handle_registry = new HashMap<>();
	public static Map<String, AdornmentMaterial> adornment_registry = new HashMap<>();

	public static void registerHeadMat(HeadMaterial mat) {
		head_registry.put(mat.getName(), mat);
	}

	public static void registerHaftMat(HaftMaterial mat) {
		haft_registry.put(mat.getName(), mat);
		System.out.println(mat.getName());
	}

	public static void registerHandleMat(HandleMaterial mat) {
		handle_registry.put(mat.getName(), mat);
	}

	public static void registerAdornmentMat(AdornmentMaterial mat) {
		adornment_registry.put(mat.getName(), mat);
	}
	
	public static HeadMaterial randomHead() {
		HeadMaterial[] heads = head_registry.values().toArray(new HeadMaterial[head_registry.values().size()]);
		return heads[rand.nextInt(heads.length)];
	}
	
	public static HaftMaterial randomHaft() {
		HaftMaterial[] hafts = haft_registry.values().toArray(new HaftMaterial[haft_registry.values().size()]);
		return hafts[rand.nextInt(hafts.length)];
	}
	
	public static HandleMaterial randomHandle() {
		HandleMaterial[] handles = handle_registry.values().toArray(new HandleMaterial[handle_registry.values().size()]);
		return handles[rand.nextInt(handles.length)];
	}
	
	public static AdornmentMaterial randomAdornment() {
		AdornmentMaterial[] adornments = adornment_registry.values().toArray(new AdornmentMaterial[adornment_registry.values().size()]);
		return adornments[rand.nextInt(adornments.length)];
	}
	
	public static boolean canReplaceMaterial(String materialName, ItemStack stack) {
		for (HeadMaterial headMat : head_registry.values()) {
			if (headMat.canReplaceMaterial(materialName)) {
				if (materialName.equals("DIAMOND") || materialName.equals("IRON") || materialName.equals("STONE") || materialName.equals("GOLD") || materialName.equals("WOOD")) {
					String domain = stack.getItem().getRegistryName().getResourceDomain();
					if (!domain.equals("minecraft")) continue;
				}
				return true;
			}
		}
		return false;
	}

}
