var collections = db.getCollectionNames();
let totalAllocatedStorage = 0;
let totalFreeSpace = 0;

for (var i = 0; i < collections.length; i++) {
	let stats = db.getCollection(collections[i]).stats();
	let colStats = db.runCommand({"collstats": collections[i]});
	let blockManager = colStats["wiredTiger"]["block-manager"];
	let freeSpace = Number(blockManager["file bytes available for reuse"]);
	let allocatedStorage = Number(blockManager["file size in bytes"]);
	let indexSize = Number(stats.totalIndexSize);

	totalAllocatedStorage += allocatedStorage + indexSize;
	totalFreeSpace += freeSpace;

	print(collections[i], allocatedStorage/1024/1024/1024, freeSpace/1024/1024/1024, indexSize/1024/1024/1024);
	
}

print("Total On Disk Storage: " + (totalAllocatedStorage + totalFreeSpace) / 1024 / 1024 / 1024 + " GB");
print("Total Free Space: " + totalFreeSpace / 1024 / 1024 / 1024 + " GB");
print("Effective Database Size: " + totalAllocatedStorage/ 1024 / 1024 / 1024 + " GB");