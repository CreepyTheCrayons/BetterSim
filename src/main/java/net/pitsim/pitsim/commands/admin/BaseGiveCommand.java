package net.pitsim.pitsim.commands.admin;

import dev.kyro.arcticapi.commands.AMultiCommand;

public class BaseGiveCommand extends AMultiCommand {
	public BaseGiveCommand(AMultiCommand base, String executor) {
		super(base, executor);
	}
}
