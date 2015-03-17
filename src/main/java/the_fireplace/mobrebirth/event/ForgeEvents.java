package the_fireplace.mobrebirth.event;

import the_fireplace.fireplacecore.FireCoreBaseFile;
import the_fireplace.mobrebirth.config.ConfigValues;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.passive.IAnimals;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemMonsterPlacer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingSpawnEvent;
import net.minecraft.entity.monster.*;

public class ForgeEvents {

public static EntityLivingBase storedEntity;
public static NBTTagCompound storedNBT;
public static double storedX;
public static double storedY;
public static double storedZ;
	
@SubscribeEvent
public void onEntityLivingDeath(LivingDropsEvent event) {
	if(ConfigValues.NATURALREBIRTH == true){
		if ((event.entityLiving instanceof IMob)) {//Checks to see if it was a Mob
			makeMobReborn(event);
		}else if ((event.entityLiving instanceof IAnimals)) {
			if (ConfigValues.SPAWNANIMALS == true){
				makeMobReborn(event);
			}
		}
	}
	else{
		if(event.source.getEntity() instanceof EntityPlayer){
			if ((event.entityLiving instanceof IMob)) {//Checks to see if it was a Mob
				makeMobReborn(event);
			}else if ((event.entityLiving instanceof IAnimals)) {
				if (ConfigValues.SPAWNANIMALS == true){
					makeMobReborn(event);
				}
			}
		}
	}
}
private void makeMobReborn(LivingDropsEvent event){
	double rand = Math.random();
	int id = EntityList.getEntityID(event.entityLiving);
	if (rand <= ConfigValues.SPAWNMOBCHANCE) {
				if (ConfigValues.SPAWNMOB == false && EntityList.entityEggs.containsKey(id)){
					ItemStack dropEgg = new ItemStack(Items.spawn_egg, 1, id);
					event.entityLiving.entityDropItem(dropEgg, 0.0F);}
				else{
					createEntity(event);
	                if(ConfigValues.EXTRAMOBCOUNT > 0){
	            		double rand2 = Math.random();
	            		if(ConfigValues.MULTIMOBMODE.toLowerCase() == "all"){
	            			if(rand2 <= ConfigValues.MULTIMOBCHANCE){
	            			int i = 0;
	            				while(i < ConfigValues.EXTRAMOBCOUNT){
	            					createEntity(event);
	            					i = i+1;
	            				}
	            			}
	            		}
	            		else{
	            			int i = 0;
	            			while(i < ConfigValues.EXTRAMOBCOUNT){
	            				if(rand2 <= ConfigValues.MULTIMOBCHANCE){
	            					createEntity(event);
	            				}
	            				i = i+1;
	            			}
	            		}
	            	}
	                
	                }
			}
	
}

private void createEntity(LivingDropsEvent event){
	EntityLivingBase entity;
	World worldIn = event.entityLiving.worldObj;
	int id = EntityList.getEntityID(event.entityLiving);
	NBTTagCompound storedData = event.entityLiving.getEntityData();
	ItemStack weapon = event.entityLiving.getHeldItem();
	entity = (EntityLivingBase) EntityList.createEntityByID(id, worldIn);
    entity.setLocationAndAngles(event.entityLiving.posX, event.entityLiving.posY, event.entityLiving.posZ, MathHelper.wrapAngleTo180_float(worldIn.rand.nextFloat() * 360.0F), 0.0F);
    entity.rotationYawHead = entity.rotationYaw;
    entity.renderYawOffset = entity.rotationYaw;
    storedData.setInteger("health", (int) event.entityLiving.getMaxHealth());
    ((EntityLivingBase) entity).readFromNBT(storedData);
    entity.setCurrentItemOrArmor(0, weapon);
	worldIn.spawnEntityInWorld(entity);
}

@SubscribeEvent
public void entityDamaged(LivingHurtEvent event){
	if(event.source.isFireDamage() && ConfigValues.SUNLIGHTAPOCALYPSEFIX == true && event.entityLiving.isEntityUndead() && event.entityLiving.worldObj.canBlockSeeSky(new BlockPos(MathHelper.floor_double(event.entityLiving.posX), MathHelper.floor_double(event.entityLiving.posY), MathHelper.floor_double(event.entityLiving.posZ)))){
		event.setCanceled(true);
	}
}
}
