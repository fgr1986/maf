
Reset

k d²T/dz² + J^2 * ro = 0		T(0)=T(ox)=T_0
								Tmax = T_0 + J^2 * ro * ox^2 / (8 * k)
J = I/a

T(z) = z^3*q/(2k) + C1 * z + C2
T(0)=T(ox)=T_0
->
T_0 = C2 -> C2 = T_0
T_0 = z_ox^3*q/(2k) + C1 * z_ox + T_0 -> C1 = -z_ox^2*q/(2k)


		---------------
		- Temperature 
		---------------
		T(z) = z^3*q/(2k) + -z_ox^2*q/(2k) * z + T_0

		---------------
		- RESET
		---------------
		Rgap = Delta*(ro_ox / (1 + gamma * F))
		Rtop = ro_m * size_top
		Rbottom = ro_m * size_bottom
		I = V( Rgap + Rtop + Rbottom)
		Vgap = V - I * Rgap
		Ea = Ea0 - alpha * q * Vgap
		d Delta / dt = A*exp( -Ea/(k T(z1) ) )
